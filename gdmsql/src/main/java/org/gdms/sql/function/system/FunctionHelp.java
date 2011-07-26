package org.gdms.sql.function.system;

import org.apache.log4j.Logger;
import org.gdms.data.InitializationException;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadAccess;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.function.Function;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableFunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

public final class FunctionHelp extends AbstractTableFunction {

        private static final Logger LOG = Logger.getLogger(FunctionHelp.class);
        
        private Metadata metadata;

        public FunctionHelp() {
                DefaultMetadata defaultMetadata = new DefaultMetadata();
                try {
                        defaultMetadata.addField("name", TypeFactory.createType(Type.STRING));
                        defaultMetadata.addField("sqlorder", TypeFactory.createType(Type.STRING));
                        defaultMetadata.addField("description", TypeFactory.createType(Type.STRING));
                        defaultMetadata.addField("type", TypeFactory.createType(Type.STRING));
                } catch (DriverException ex) {
                        throw new InitializationException(ex);
                }
                metadata = defaultMetadata;
        }

        @Override
        public ReadAccess evaluate(SQLDataSourceFactory dsf, ReadAccess[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");

                String[] functions = FunctionManager.getFunctionNames();

                try {
                        GenericObjectDriver genericObjectDriver = new GenericObjectDriver(
                                metadata);
                        for (String function : functions) {

                                Function fct = FunctionManager.getFunction(function);
                                String type = null;
                                if (fct.isScalar()) {
                                        type = "Scalar Function";
                                } else if (fct.isTable()) {
                                        type = "Table Function";
                                } else if (fct.isAggregate()) {
                                        type = "Aggregate Function";
                                } else if (fct.isExecutor()) {
                                        type = "Executor Function";
                                }
                                genericObjectDriver.addValues(ValueFactory.createValue(fct.getName()), ValueFactory.createValue(fct.getSqlOrder()), ValueFactory.createValue(fct.getDescription()), ValueFactory.createValue(type));
                        }

                        return genericObjectDriver.getTable("main");
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }

        }

        @Override
        public String getDescription() {
                return "Create a table with all functions";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return metadata;
        }

        @Override
        public String getName() {
                return "FunctionHelp";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT FunctionHelp()";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.ANY)
                        };
        }
}