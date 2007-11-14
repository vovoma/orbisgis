package org.gdms.source;

import java.io.File;
import java.io.IOException;

import org.gdms.data.DataSourceCreation;
import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFinalizationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.driverManager.DriverManager;
import org.gdms.sql.instruction.TableNotFoundException;

public interface SourceManager {

	/**
	 * Sets the driver manager used to load the drivers of the sources
	 *
	 * @param dm
	 */
	public abstract void setDriverManager(DriverManager dm);

	/**
	 * Adds a listener to the events in this class
	 *
	 * @param e
	 * @return
	 */
	public abstract boolean addSourceListener(SourceListener e);

	/**
	 * Removes a listener to the events in this class
	 *
	 * @param o
	 * @return
	 */
	public abstract boolean removeSourceListener(SourceListener o);

	/**
	 * Removes all the information about the sources
	 *
	 * @throws IOException
	 */
	public abstract void removeAll() throws IOException;

	/**
	 * Try to remove the information about the source with the name or alias
	 * specified
	 *
	 * @param name
	 * @return true if the source was found and removed and false if the source
	 *         was not found
	 * @throws IllegalStateException
	 *             If some source depends on the source being removed
	 */
	public abstract boolean remove(String name) throws IllegalStateException;

	/**
	 * Registers a file with the specified name
	 *
	 * @param name
	 *            Name to register with
	 * @param file
	 *            file to register
	 * @throws SourceAlreadyExistsException
	 */
	public abstract void register(String name, File file)
			throws SourceAlreadyExistsException;

	/**
	 * Registers a database table with the specified name
	 *
	 * @param name
	 *            Name to register
	 * @param dbTable
	 *            source to register
	 */
	public abstract void register(String name, DBSource dbTable)
			throws SourceAlreadyExistsException;

	/**
	 * Registers a object with the specified name
	 *
	 * @param name
	 *            Name to register with
	 * @param driver
	 *            object to register
	 */
	public abstract void register(String name, ObjectDriver driver)
			throws SourceAlreadyExistsException;

	/**
	 * Generic register method
	 *
	 * @param name
	 *            Name to register with
	 * @param def
	 *            definition of the source
	 */
	public abstract void register(String name, DataSourceDefinition def)
			throws SourceAlreadyExistsException;

	/**
	 * Get's a unique id
	 *
	 * @return unique id
	 */
	public abstract String getUID();

	/**
	 * Registers generating the name automatically
	 *
	 * @param file
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(File file);

	/**
	 * Registers generating the name automatically
	 *
	 * @param dbTable
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(DBSource dbTable);

	/**
	 * Registers generating the name automatically
	 *
	 * @param driver
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(ObjectDriver driver);

	/**
	 * Registers generating the name automatically
	 *
	 * @param def
	 * @return the name of the registered source
	 */
	public abstract String nameAndRegister(DataSourceDefinition def);

	/**
	 * Adds a new name to the specified data source name. The main name of the
	 * data source will not change but the new name can be used to refer to the
	 * source in the same way as the main one
	 *
	 * @param dsName
	 * @param newName
	 * @throws TableNotFoundException
	 */
	public abstract void addName(String dsName, String newName)
			throws TableNotFoundException, SourceAlreadyExistsException;

	/**
	 * Modifies the name of the specified source. If modifies either the main
	 * name either the aliases
	 *
	 * @param dsName
	 * @param newName
	 * @throws SourceAlreadyExistsException
	 */
	public abstract void rename(String dsName, String newName)
			throws SourceAlreadyExistsException;

	/**
	 * @param sourceName
	 * @return true if there is a source with the specified name, false
	 *         otherwise
	 */
	public abstract boolean exists(String sourceName);

	/**
	 * Gets the main name of the source
	 *
	 * @param dsName
	 * @return
	 * @throws NoSuchTableException
	 *             If there is no source with the specified name
	 */
	public abstract String getMainNameFor(String dsName)
			throws NoSuchTableException;

	/**
	 * Called to free resources
	 *
	 * @throws DataSourceFinalizationException
	 */
	public abstract void shutdown() throws DataSourceFinalizationException;

	/**
	 * @return true if there is no source in the manager and false otherwise
	 */
	public abstract boolean isEmpty();

	/**
	 * Creates a source and returns a definition of the source that can be used
	 * to register it. This method does not register the created source
	 *
	 * @param dsc
	 *            creation object
	 * @return
	 * @throws DriverException
	 */
	public abstract DataSourceDefinition createDataSource(DataSourceCreation dsc)
			throws DriverException;

	/**
	 * Gets the source with the specified name
	 *
	 * @param name
	 * @return null if there is no source with that name
	 */
	public Source getSource(String name);

	/**
	 * Sets the directory where the registry of sources and its properties is
	 * stored. Each call to saveStatus will serialize the content of this
	 * manager to the specified directory
	 *
	 * @param newDir
	 * @throws DriverException
	 */
	public abstract void setSourceInfoDirectory(String newDir)
			throws DriverException;

	/**
	 * Method for debugging purposes that obtains a snapshot of the system
	 *
	 * @return
	 * @throws IOException
	 */
	public abstract String getMemento() throws IOException;

	/**
	 * saves all the information about the sources in the directory specified by
	 * the last call to setSourceInfoDirectory
	 *
	 * @throws DriverException
	 */
	public abstract void saveStatus() throws DriverException;

	/**
	 * Gets the directory where the information of sources is stored
	 *
	 * @return
	 */
	public abstract File getSourceInfoDirectory();

	/**
	 * Registers an sql instruction.
	 *
	 * @param name
	 *            name to register
	 * @param sql
	 *            instruction to register
	 */
	public abstract void register(String name, String sql)
			throws SourceAlreadyExistsException;

	/**
	 * Gets the driver manager
	 *
	 * @return
	 */
	public DriverManager getDriverManager();

	/**
	 * Gets the name of the driver that accesses this source
	 *
	 * @param sourceName
	 * @return
	 * @throws NoSuchTableException
	 */
	public abstract String getDriverName(String sourceName) throws NoSuchTableException;

	/**
	 * Removes the specified secondary name.
	 *
	 * @param secondName
	 */
	public abstract void removeName(String secondName);

}