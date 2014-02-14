package com.xiledsystems.AlternateJavaBridgelib.components.altbridge;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.collect.TwinList;
import com.xiledsystems.AlternateJavaBridgelib.components.altbridge.util.SdkLevel;
import com.xiledsystems.AlternateJavaBridgelib.components.errors.SimpleSQLBatchException;


public class SimpleSQL extends AndroidNonvisibleComponent implements OnDestroySvcListener, OnResumeListener, OnStopListener {

	/**
	 * Use this as the columns to return if you want to pull everything from the
	 * database including the rowid numbers.
	 */
	public final static String[] INCLUDE_ROWID = { "*RowId*" };
	private final static String[] OPERATORS = { "=", "!=", "<", "<=", ">", ">=", "<>" };
	private final static String[] OP_ESCAPED = { "=", "!=", "\\<", "\\<=", "\\>", "\\>=", "\\<\\>" };
	private static final String AND = "AND";
	private static final String OR = "OR";
	private static final String[] OPS = { AND, OR };
	private static final String TAG = "SimpleSQL";
	private static final String COLUMN_ID = "_id";
	
	private String BUILDER_SAVE = "DBBuilder.sv";
	private String DATABASE_NAME;
	private int DATABASE_VERSION = 1;
	private static String TABLE_NAME = "table1";
	
	private String DATABASE_CREATE;
	private BigDBSqlOpenHelper dbHelper;
	private DBBuilder dBuilder;
	private int batchCount;
	private int maxBatchCount = 500;
	private OnSQLUpgrade upgradeIntf;
	private Set<String> addedTableNames = new HashSet<String>();

	private final Object dbLock = new Object();

	private SQLiteDatabase sDb = null;
	private boolean keepInMem = true;

	private boolean dontStop;
	private boolean batch;
	private TinyDB storage;
	private boolean addingTable;
	private boolean forcedBuilder;
	
	/**
	 * 
	 * Constructor for SimpleSQL component. This requires you to define the
	 * table structure using DBBuilder before creating the database.
	 * 
	 * @param form
	 *            Always use this
	 * @param builder
	 *            The table/column configuration helper. If builder is null,
	 *            this will cause a RuntimeException.
	 */
	public SimpleSQL(Form form, DBBuilder builder) {
		super(form);
		DATABASE_NAME = getDBName(builder, form);
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		storage = new TinyDB(form);
		storage.LogErrors(false);
		BUILDER_SAVE = getBuilderSaveName();
		Object b = storage.GetValue(BUILDER_SAVE);
		if (b instanceof DBBuilder) {
			dBuilder = (DBBuilder) b;
		} else {
			dBuilder = builder;
		}
		if (dBuilder.Version() > 1) {
			DATABASE_VERSION = dBuilder.Version();
		}
		if (dBuilder.DBName() != null && !dBuilder.DBName().equals("")) {
			DATABASE_NAME = dBuilder.DBName();
		}
		form.registerForOnResume(this);
		form.registerForOnStop(this);
		dbHelper = new BigDBSqlOpenHelper(form.$context());
		checkBuilderForDuplicateTables(dBuilder);
		// db = dbHelper.getWritableDatabase();
		if (builder != dBuilder) {
			storage.StoreValue(BUILDER_SAVE, dBuilder);
		}
		keepDbInMemory(keepInMem);
	}

	public SimpleSQL(Form form, DBBuilder builder, boolean dontRegister) {
		super(form);
		DATABASE_NAME = getDBName(builder, form);
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		storage = new TinyDB(form);
		storage.LogErrors(false);
		BUILDER_SAVE = getBuilderSaveName();
		Object b = storage.GetValue(BUILDER_SAVE);
		if (b instanceof DBBuilder) {
			dBuilder = (DBBuilder) b;
		} else {
			dBuilder = builder;
		}
		if (dBuilder.Version() > 1) {
			DATABASE_VERSION = dBuilder.Version();
		}
		if (dBuilder.DBName() != null && !dBuilder.DBName().equals("")) {
			DATABASE_NAME = dBuilder.DBName();
		}
		dbHelper = new BigDBSqlOpenHelper(form.$context());
		// db = dbHelper.getWritableDatabase();
		checkBuilderForDuplicateTables(dBuilder);
		if (builder != dBuilder) {
			storage.StoreValue(BUILDER_SAVE, dBuilder);
		}
		keepDbInMemory(keepInMem);
	}

	/**
	 * 
	 * Constructor for SimpleSQL component. This requires you to define the
	 * table structure using DBBuilder before creating the database.
	 * 
	 * @param formservice
	 *            Always use this
	 * @param builder
	 *            The table/column configuration helper. If builder is null,
	 *            this will cause a RuntimeException.
	 */
	public SimpleSQL(FormService formservice, DBBuilder builder) {
		super(formservice);
		DATABASE_NAME = getDBName(builder, formservice);
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		storage = new TinyDB(formservice);
		storage.LogErrors(false);
		BUILDER_SAVE = getBuilderSaveName();
		Object b = storage.GetValue(BUILDER_SAVE);
		if (b instanceof DBBuilder) {
			dBuilder = (DBBuilder) b;
		} else {
			dBuilder = builder;
		}
		if (dBuilder.Version() > 1) {
			DATABASE_VERSION = dBuilder.Version();
		}
		if (dBuilder.DBName() != null && !dBuilder.DBName().equals("")) {
			DATABASE_NAME = dBuilder.DBName();
		}
		formservice.registerForOnDestroy(this);
		dbHelper = new BigDBSqlOpenHelper(formservice.$context());
		// db = dbHelper.getWritableDatabase();
		checkBuilderForDuplicateTables(dBuilder);
		if (builder != dBuilder) {
			storage.StoreValue(BUILDER_SAVE, dBuilder);
		}
		keepDbInMemory(keepInMem);
	}
	
	
	public SimpleSQL(FormService formservice, boolean forceBuilder, DBBuilder builder) {
		super(formservice);
		DATABASE_NAME = getDBName(builder, formservice);
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		storage = new TinyDB(formservice);
		storage.LogErrors(false);
		BUILDER_SAVE = getBuilderSaveName();		
		dBuilder = builder;
		forcedBuilder = forceBuilder;
		
		if (dBuilder.Version() > 1) {
			DATABASE_VERSION = dBuilder.Version();
		}
		if (dBuilder.DBName() != null && !dBuilder.DBName().equals("")) {
			DATABASE_NAME = dBuilder.DBName();
		}
		formservice.registerForOnDestroy(this);
		dbHelper = new BigDBSqlOpenHelper(formservice.$context());
		// db = dbHelper.getWritableDatabase();
		checkBuilderForDuplicateTables(dBuilder);
		if (builder != dBuilder) {
			storage.StoreValue(BUILDER_SAVE, dBuilder);
		}
		keepDbInMemory(keepInMem);
	}

	public SimpleSQL(FormService formservice, DBBuilder builder, boolean dontRegister) {
		super(formservice);
		storage = new TinyDB(formservice);
		storage.LogErrors(false);
		DATABASE_NAME = getDBName(builder, formservice);
		BUILDER_SAVE = getBuilderSaveName();
		if (builder == null) {
			throw new RuntimeException("SimpleSQL: Builder passed to DB is null!");
		}
		Object b = storage.GetValue(BUILDER_SAVE);
		if (b instanceof DBBuilder) {
			dBuilder = (DBBuilder) b;
		} else {
			dBuilder = builder;
		}
		if (dBuilder.Version() > 1) {
			DATABASE_VERSION = dBuilder.Version();
		}
		if (dBuilder.DBName() != null && !dBuilder.DBName().equals("")) {
			DATABASE_NAME = dBuilder.DBName();
		}
		dbHelper = new BigDBSqlOpenHelper(formservice.$context());
		// db = dbHelper.getWritableDatabase();
		checkBuilderForDuplicateTables(dBuilder);
		if (builder != dBuilder) {
			storage.StoreValue(BUILDER_SAVE, dBuilder);
		}
		
		keepDbInMemory(keepInMem);
	}
	
	private static String getDBName(DBBuilder dBuilder, Context context) {
		String db = "";
		if (dBuilder != null && !dBuilder.DBName().equals("")) {
			db = dBuilder.DBName();
		} else {			
			db = context.getApplicationContext().getPackageName() + "-SQL.db";			 
		}
		return db;
	}
	
	private static void checkBuilderForDuplicateTables(DBBuilder builder) {
		int size = builder.TableCount();
		ArrayList<Integer> remove = new ArrayList<Integer>();
		for (int i = (size - 1); i > -1; i--) {
			String table = builder.Table(i);
			for (int x = 0; x < size; x++) {
				if (x != i && builder.Table(x).equals(table)) {
					remove.add(x);
				}
			}
		}
		for (Integer i : remove) {
			builder.removeTable(i);
		}
	}

	private void checkSdkLevel() {
		if (SdkLevel.getLevel() < SdkLevel.LEVEL_ICE_CREAM_SANDWICH) {
			keepDbInMemory(true);
		}
	}

	public void setOnUpgradeListener(OnSQLUpgrade listener) {
		upgradeIntf = listener;
	}

	public void keepDbInMemory(boolean keep) {
		if (keep) {
			if ((sDb == null || !sDb.isOpen())) {
				try {
					sDb = dbHelper.getWritableDatabase();
				} catch (SQLiteException e) {
					if (forcedBuilder) {
						String msg = e.getMessage();
						if (msg.contains("downgrade database from version")) {
							String ver = msg.split("version")[1];
							String[] vers = ver.split("to");
							DATABASE_VERSION = Convert.Int(vers[0].trim());
							dBuilder.Version(DATABASE_VERSION);
							saveBuilder(dBuilder);
							if (DATABASE_NAME == null || DATABASE_NAME.equals("")) {
								DATABASE_NAME = getDBName(dBuilder, getContext());
							}
							dbHelper = new BigDBSqlOpenHelper(getContext());
							sDb = dbHelper.getWritableDatabase();
						}
					} else {
						throw new SQLException(e.getMessage());
					}
				}
			}
			keepInMem = true;
		} else {			
			sDb = null;
			keepInMem = false;
			checkSdkLevel();			
		}
	}
	
	@SuppressWarnings("static-access")
	public void closeDB() {
		synchronized (dbLock) {
			if (sDb == null) {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				if (db != null) {
					db.close();
					db.releaseMemory();
				}
			} else {
				sDb.close();
				sDb.releaseMemory();
			}
		}
	}

	/**
	 * 
	 * @return the SQLite Database backing the SimpleSQL class.
	 */
	public synchronized SQLiteDatabase getSQLDB() {
		if (sDb == null) {
			return dbHelper.getWritableDatabase();
		} else {
			return sDb;
		}
	}

	/**
	 * Use this to open a seperate db file (Perfect for backup and restoring
	 * your db)
	 * 
	 * @param path
	 *            The absolute path to the db file
	 * @throws SQLiteException
	 *             - This is thrown if the db is corrupt, or not a Sqlite db
	 *             file. It may also be thrown when the builder doesn't match
	 *             the table/columns in the specified db file
	 */
	public void openDB(String path) throws SQLiteException {
		synchronized (dbLock) {
			if (path != null) {
				if (sDb == null) {
					SQLiteDatabase db;
					try {
						db = dbHelper.getWritableDatabase();
						if (db != null && db.isOpen()) {
							db.close();
						}
					} catch (SQLException e) {
						// Can't open the db, just ignore, as we're opening a new
						// one anyways.
					}

					db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
					dBuilder = getBuilderInfo(db, dBuilder.DBName());
					DATABASE_NAME = getDBName(dBuilder, getContext());
					DATABASE_VERSION = dBuilder.Version();
					dbHelper = new BigDBSqlOpenHelper(getContext());
					BUILDER_SAVE = getBuilderSaveName();
					storage.StoreValue(BUILDER_SAVE, dBuilder);
					resetDB();
				} else {
					if (sDb != null && sDb.isOpen()) {
						sDb.close();
					}
					sDb = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE);
					dBuilder = getBuilderInfo(sDb, dBuilder.DBName());					
					DATABASE_NAME = getDBName(dBuilder, getContext());
					DATABASE_VERSION = dBuilder.Version();
					dbHelper = new BigDBSqlOpenHelper(getContext());
					BUILDER_SAVE = getBuilderSaveName();
					storage.StoreValue(BUILDER_SAVE, dBuilder);
					resetDB();
				}
			}
		}
	}
	
	public ArrayList<String> getTablesFromDB() {
		return getTablesFromDB(getDataBase());
	}
	
	private static ArrayList<String> getTablesFromDB(SQLiteDatabase db) {
		ArrayList<String> tables = new ArrayList<String>();
		Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				String table = cursor.getString(cursor.getColumnIndex("name"));
				if (!table.equals("android_metadata") && !table.equals("dummy") && !table.equals("sqlite_sequence") && !table.equals("_id")) {
					tables.add(table);
				}
				cursor.moveToNext();
			}
		}
		cursor.close();
		return tables;
	}

	private static DBBuilder getBuilderInfo(SQLiteDatabase db, String dbPath) {
		DBBuilder dBuilder = new DBBuilder();
		if (!dbPath.equals("")) {
			dBuilder.DBName(dbPath);
		}
		ArrayList<String> tables = getTablesFromDB(db);
		tables = stripTableNames(tables);
		int size = tables.size();
		for (int i = 0; i < size; i++) {
			String[] columnNames;
			Cursor cursor = db.rawQuery("SELECT * FROM " + checkColumnName(tables.get(i)) + " LIMIT 1", null);
			if (cursor != null) {
				boolean keepGoing = true;
				while (keepGoing) {
					columnNames = cursor.getColumnNames();
					columnNames = stripColumnNames(columnNames);
					dBuilder.addTable(tables.get(i), columnNames);
					keepGoing = false;
					cursor.close();
				}
			}
		}
		int dbVer = db.getVersion();
		if (dbVer == 0) {
			dbVer = 1;
		}
		dBuilder.Version(dbVer);
		return dBuilder;
	}

	private static String[] stripColumnNames(String[] columns) {
		String[] cols = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			if (columns[i].startsWith("'") && columns[i].endsWith("'")) {
				cols[i] = columns[i].replace("'", "");
			} else {
				cols[i] = columns[i];
			}
		}
		cols = removeIdColumn(cols);
		return cols;
	}

	private static String[] removeIdColumn(String[] cols) {
		ArrayList<String> list = new ArrayList<String>();
		for (String s : cols) {
			if (!s.equals("_id")) {
				list.add(s);
			}
		}
		return list.toArray(new String[0]);
	}

	private static ArrayList<String> stripTableNames(ArrayList<String> columns) {
		ArrayList<String> cols = new ArrayList<String>();
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).startsWith("'") && columns.get(i).endsWith("'")) {
				cols.add(columns.get(i).replace("'", ""));
			} else {
				cols.add(columns.get(i));
			}
		}
		return cols;
	}

	private String getBuilderSaveName() {
		if (DATABASE_NAME.contains(".db")) {
			return DATABASE_NAME.split("\\.db")[0] + ".bsv";
		} else {
			return DATABASE_NAME + ".bsv";
		}
	}

	/**
	 * Returns the path of the database file.
	 * 
	 * @return
	 */
	public String getDBPath() {
		synchronized (dbLock) {
			// dbCheck();
			if (sDb == null) {
				return dbHelper.getWritableDatabase().getPath();
			} else {
				return sDb.getPath();
			}
		}
	}

	/**
	 * Use this when inserting, or updating a lot of entries. You MUST remember
	 * to run FinishBatch() after you are done with your inserts/updates. This
	 * will not work for reading the database.
	 * 
	 */
	public void BatchWrite() {
		synchronized (dbLock) {
			batch = true;
			if (sDb == null) {
				dbHelper.getWritableDatabase().beginTransaction();
			} else {
				sDb.beginTransaction();
			}
		}
	}

	/**
	 * Use this after performing a batch of inserts/updates, (if you have run
	 * BatchWrite() prior).
	 * 
	 * This will catch the IllegalStateException which can get thrown if
	 * FinishBatch is called when nothing was added to the db.
	 */
	public void FinishBatch() {
		synchronized (dbLock) {
			batch = false;
			try {
				if (sDb == null) {
					dbHelper.getWritableDatabase().setTransactionSuccessful();
					dbHelper.getWritableDatabase().endTransaction();
					batchCount = 0;
				} else {
					sDb.setTransactionSuccessful();
					sDb.endTransaction();
				}
			} catch (IllegalStateException e) {
				// No transactions processed, just continue on
			}
		}
	}

	/**
	 * Only needed in rare situations. This just sets the boolean which controls
	 * the isInBatchMode method. Only use this if you start batch mode, then
	 * need to stop batch mode, but haven't actually written anything to the db.
	 * 
	 */
	public void unBatch() {
		batch = false;
		batchCount = 0;
	}

	/**
	 * Use this method carefully. The default is 2000 transactions per batch.
	 * You can use this method to override the default to another number. As you
	 * increase the number, you are increasing the amount of RAM the db takes
	 * up, and could cause out of memory crashes if it's set too large.
	 * 
	 * @param count
	 *            the maximum amount of transactions allowed in a BatchWrite
	 */
	public void MaxBatchCount(int count) {
		maxBatchCount = count;
	}

	/**
	 * 
	 * @return The maximum transactions in a batch. The default is 2000. The
	 *         higher you go, the more memory it will take up, possibly leading
	 *         to Out of Memory crashes.
	 */
	public int getMaxBatchCount() {
		return maxBatchCount;
	}

	/**
	 * 
	 * This is for advanced users! This method returns a Cursor object, which
	 * you'll have to deal with. Make sure it is closed when you are done, or
	 * you'll run into memory leaks.
	 * 
	 * @param queryString
	 *            The query string to pass to the sqlite method db.rawQuery()
	 * @param selectionArgs
	 *            any values used in the where statement
	 * @return the cursor object with the data returned. null if the db is in
	 *         batch mode
	 */
	public Cursor AdvancedQuery(String queryString, String[] selectionArgs) {
		synchronized (dbLock) {
			if (!batch) {
				if (sDb == null) {
					return dbHelper.getWritableDatabase().rawQuery(queryString, selectionArgs);
				} else {
					return sDb.rawQuery(queryString, selectionArgs);
				}
			}
			return null;
		}
	}

	/**
	 * 
	 * Insert data into the database in the specified table. The method returns
	 * the rowId resulting from the add to the database.
	 * 
	 * @param table
	 *            - The table to add data to
	 * 
	 * @param items
	 *            - The data to put into the database
	 * @return returns a long of the rowId. -1 will be returned if the insert
	 *         failed.
	 */
	public long Insert(String table1, String... items) {

		synchronized (dbLock) {
			// dbCheck();
			SQLiteDatabase db = getDataBase();
			if (batch) {
				if (batchCount >= maxBatchCount) {
					db.setTransactionSuccessful();
					db.endTransaction();
					batchCount = 0;
					db.beginTransaction();
				}
				batchCount++;
			}
			if (items != null) {
				int amt = items.length;
				int tableid = dBuilder.TableId(table1);
				ContentValues values = new ContentValues();
				for (int i = 0; i < amt; i++) {
					String column = checkColumnName(dBuilder.Column(tableid)[i]);
					values.put(column, items[i]);
				}
				String table = checkColumnName(table1);
				try {
					long rowId = db.insert(table, null, values);
					return rowId;
				} catch (SQLException e) {
					Log.e("SimpleSQL", "Unable to insert data. Either the table doesn't exist, or incorrect amount of data items." + table1);
					e.printStackTrace();
					return -1;
				}
			} else {
				try {
					String table = checkColumnName(table1);
					long rowid = db.insert(table, null, null);
					return rowid;
				} catch (SQLException e) {
					Log.e("SimpleSQL", "Unable to insert data. Either the table doesn't exist, or incorrect amount of data items." + table1);
					e.printStackTrace();
					return -1;
				}
			}
		}
	}

	/**
	 * 
	 * Use this method to insert data into one specific column in a table.
	 * 
	 * @param table
	 *            The table to insert to
	 * @param columntoaddto
	 *            The column you want to add data into
	 * @param data
	 *            The data to store
	 * @return The rowid of the newly inserted data
	 */
	public long Insert(String table, String columntoaddto, String data) {
		synchronized (dbLock) {
			// dbCheck();
			SQLiteDatabase db = getDataBase();
			if (batch) {
				if (batchCount >= maxBatchCount) {
					db.setTransactionSuccessful();
					db.endTransaction();
					batchCount = 0;
					db.beginTransaction();
				}
				batchCount++;
			}
			ContentValues values = new ContentValues();
			values.put(columntoaddto, data);
			try {
				long rowid = db.insert(checkColumnName(table), null, values);
				return rowid;
			} catch (SQLException e) {
				Log.e("SimpleSQL", "Unable to insert data. Either the table doesn't exist, or incorrect amount of data items." + table);
				e.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * Clear all records (rows) in the specified table. This is destructive,
	 * once run, that table's data will be gone forever.
	 * 
	 * @param table
	 *            The table to clear
	 * 
	 * @return The number of rows deleted from the table. If the table doesn't
	 *         exist, -1 will be thrown.
	 */
	public int ClearTable(String table) {
		synchronized (dbLock) {
			if (dBuilder.containsTable(table)) {
				SQLiteDatabase db = getDataBase();
				table = table.trim();
				int rmvd = db.delete(fixTableName(table), "1", null);
				db.execSQL("VACUUM");
				db.close();
				db = null;
				if (keepInMem) {
					sDb = dbHelper.getWritableDatabase();
				} else {
					db = dbHelper.getWritableDatabase();
				}
				return rmvd;
			}
			return -1;
		}
	}

	private static String fixTableName(String tableName) {
		return checkColumnName(tableName);
	}

	/**
	 * 
	 * Deletes the table from the database. This will delete all data in the
	 * table as well.
	 * 
	 * @param table
	 *            The table to delete
	 * @return False if the table wasnt found.
	 */
	public boolean DeleteTable(String table) {
		synchronized (dbLock) {
			if (dBuilder.removeTable(table)) {
				SQLiteDatabase db;
				boolean b = false;
				if (sDb == null) {
					db = dbHelper.getWritableDatabase();
				} else {
					b = true;
					db = sDb;
				}
				db.execSQL("DROP TABLE IF EXISTS " + fixTableName(table));
				db.close();
				db = null;
				storage.StoreValue(BUILDER_SAVE, dBuilder);
				if (b) {
					sDb = dbHelper.getWritableDatabase();
				} else {
					db = dbHelper.getWritableDatabase();
				}
				return true;
			} else {
				return false;
			}
		}
	}
	
	public void ForcedBuilder(boolean forced) {
		forcedBuilder = forced;
	}
	
	public void forceDeleteTable(String table) {
		synchronized (dbLock) {
			dBuilder.removeTable(table);
			SQLiteDatabase db;
			boolean b = false;
			if (sDb == null) {
				db = dbHelper.getWritableDatabase();
			} else {
				b = true;
				db = sDb;
			}
			db.execSQL("DROP TABLE IF EXISTS " + fixTableName(table));
			db.close();
			db = null;
			storage.StoreValue(BUILDER_SAVE, dBuilder);
			if (b) {
				sDb = dbHelper.getWritableDatabase();
			} else {
				db = dbHelper.getWritableDatabase();
			}
		}
	}

	/**
	 * 
	 * Update the data in an existing row.
	 * 
	 * @param table
	 *            The table the row exists in
	 * @param column
	 *            The column of the data you'd like to change
	 * @param rowid
	 *            The rowid where the data resides
	 * @param data
	 *            The new data to store in this location
	 */
	public void Update(String table, String column, long rowid, String data) {
		synchronized (dbLock) {
			// dbCheck();
			SQLiteDatabase db = getDataBase();
			if (batch) {
				if (batchCount >= maxBatchCount) {
					db.setTransactionSuccessful();
					db.endTransaction();
					batchCount = 0;
					db.beginTransaction();
				}
				batchCount++;
			}
			ContentValues values = new ContentValues();
			values.put(column, data);
			String where = "_id=?";
			String[] whereArgs = { String.valueOf(rowid) };
			db.update(fixTableName(table), values, where, whereArgs);
		}
	}

	/**
	 * "Simplified" query method for returning data from the database. This can
	 * support one or more where statements combined by either AND, or OR. If you need
	 * to run a more complicated query, use Query3.
	 * 
	 * @param table
	 *            The table to search
	 * @param whereStatement
	 *            Your where statement. Do NOT include the word where ex:
	 *            "name=John AND purchases>25"
	 * @param ColumnsToReturn
	 *            The columns of data you want returned. Using null here will
	 *            return all columns.
	 * @return An ArrayList<ArrayList<String>> of all the data returned from the
	 *         database.
	 */
	public ArrayList<ArrayList<String>> Query(String table1, String whereStatement, String[] ColumnsToReturn) {
		String[] whereArgs = null;
		if (!batch) {
			ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
			if (whereStatement != null) {
				String[] results = processWhere(whereStatement);
				whereStatement = results[0];
				whereArgs = processArgsArray(results);
			} else {
				whereArgs = null;
			}
			String select = buildSQLSelectStatement(table1, ColumnsToReturn, whereStatement, null, null, null, null, dBuilder);
			SQLiteDatabase db = getDataBase();
			synchronized (dbLock) {				
				biglist = selectFromDB(db, select, whereArgs);
			}
			return biglist;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * "Simplified" query method for returning data from the database. This can
	 * support one or more where statements combined by either AND, or OR. If you need
	 * to run a more complicated query, use Query3. This Query is only different
	 * from Query in that you can set a limit of the amount of rows returned.
	 * 
	 * @param table
	 *            The table to search
	 * @param whereStatement
	 *            Your where statement. Do NOT include the word where ex:
	 *            "name=John AND purchases>25"
	 * @param limit
	 *            The maximum amount of rows you want to pull from the database
	 * @param ColumnsToReturn
	 *            The columns of data you want returned.
	 * @return An ArrayList<ArrayList<String>> of all the data returned from the
	 *         database.
	 */
	public ArrayList<ArrayList<String>> Query(String table1, String whereStatement, int limit, String... ColumnsToReturn) {
		String[] whereArgs = null;
		if (!batch) {
			ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
			if (whereStatement != null) {
				String[] results = processWhere(whereStatement);
				whereStatement = results[0];
				whereArgs = processArgsArray(results);
			} else {
				whereArgs = null;
			}
			String select = buildSQLSelectStatement(table1, ColumnsToReturn, whereStatement, null, null, null, limit+"", dBuilder);
			SQLiteDatabase db = getDataBase();
			synchronized (dbLock) {				
				biglist = selectFromDB(db, select, whereArgs);
			}
			return biglist;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	/**
	 * For more advanced users. This allows you to pass arguments to the db just
	 * like the "regular" android sqlite wants them. Basically, if you need to
	 * use a really complicated where statement, this is how you'll have to do
	 * it.
	 * 
	 * @param table
	 *            The table to search
	 * @param columns
	 *            The columns you'd like returned
	 * @param selection
	 *            This is the where statement. Use ? for all values here ( ex:
	 *            name=? AND number=? AND longitude>? )
	 * @param selectionArgs
	 *            This is a string array of the values. So, from the above
	 *            example ( new String[] { "John", "5555555555", "43" } )
	 * @param groupBy
	 *            more options for the cursor, usually just null
	 * @param having
	 *            again more options for the cursor, usually null
	 * @param orderBy
	 *            yet again, same as above, usually null
	 * @return An ArrayList<ArrayList<String>> of the results. This is a list of
	 *         a list. (Each inner list contains a row of data)
	 */
	public ArrayList<ArrayList<String>> Query3(String table1, String[] columns, String selection, String[] selectionArgs, String groupBy,
			String having, String orderBy) {
		if (!batch) {
			ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();			
			String select = buildSQLSelectStatement(table1, columns, selection, groupBy, having, orderBy, null, dBuilder);
			SQLiteDatabase db = getDataBase();
			synchronized (dbLock) {				
				biglist = selectFromDB(db, select, selectionArgs);
			}
			return biglist;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	/**
	 * For more advanced users. This allows you to pass arguments to the db just
	 * like the "regular" android sqlite wants them. Basically, if you need to
	 * use a really complicated where statement, this is how you'll have to do
	 * it.
	 * 
	 * @param table
	 *            The table to search
	 * @param columns
	 *            The columns you'd like returned
	 * @param selection
	 *            This is the where statement. Use ? for all values here ( ex:
	 *            name=? AND number=? AND longitude>? )
	 * @param selectionArgs
	 *            This is a string array of the values. So, from the above
	 *            example ( new String[] { "John", "5555555555", "43" } )
	 * @param groupBy
	 *            more options for the cursor, usually just null
	 * @param having
	 *            again more options for the cursor, usually null
	 * @param orderBy
	 *            yet again, same as above, usually null
	 * @return An ArrayList<ArrayList<String>> of the results. This is a list of
	 *         a list. (Each inner list contains a row of data)
	 */
	public ArrayList<ArrayList<String>> Query3(String table1, String[] columns, String selection, String[] selectionArgs, int limit, String groupBy,
			String having, String orderBy) {
		if (!batch) {
			ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();			
			String select = buildSQLSelectStatement(table1, columns, selection, groupBy, having, orderBy, limit+"", dBuilder);
			SQLiteDatabase db = getDataBase();
			synchronized (dbLock) {				
				biglist = selectFromDB(db, select, selectionArgs);
			}
			return biglist;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	public static String buildSQLSelectStatement(String table, String[] columns, String where, String groupBy, String having, 
			String orderBy,	String limit, DBBuilder dBuilder) {
		
		String state = "SELECT ";
		if (columns == null || columns == INCLUDE_ROWID) {
			columns = new String[] { "*" };
		} else if (columns.length == 0) {
			int i = dBuilder.TableId(table);
			columns = dBuilder.Column(i);
		}
		if (columns.length < 2) {
			if (!columns[0].equals("*")) {
				state += checkColumnName(columns[0]);
			} else {
				// No need to wrap * in quotes, so we just add it as such
				state += columns[0];
			}
		} else {
			for (int i = 0; i < columns.length; i++) {
				state += checkColumnName(columns[i]);
				if (i < (columns.length - 1)) {
					state += ", ";
				}
			}
		}
		state += " FROM " + checkTableName(table);
		if (where != null) {
			state += " WHERE " + where;
		}
		if (groupBy != null) {
			state += " GROUPBY " + groupBy;
		}
		if (having != null) {
			state += " HAVING " + having;
		}
		if (orderBy != null) {
			state += " ORDERBY " + orderBy;
		}
		if (limit != null) {
			state += " LIMIT " + limit;
		}
		return state;
	}

	private static String[] processArgsArray(String[] results) {
		String[] whereArgs = new String[results.length - 1];
		for (int i = 0; i < (results.length-1); i++) {
			whereArgs[i] = results[i + 1];
		}
		return whereArgs;
	}
	
	private static ArrayList<ArrayList<String>> selectFromDB(SQLiteDatabase db, String statement, String[] args) {
		ArrayList<ArrayList<String>> biglist = new ArrayList<ArrayList<String>>();
		Cursor cursor = db.rawQuery(statement, args);
		if (cursor.moveToFirst()) {
			int length = cursor.getColumnCount();
			do {
				ArrayList<String> smallist = new ArrayList<String>();
				for (int i = 0; i < length; i++) {
					smallist.add(cursor.getString(i));
				}
				biglist.add(smallist);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return biglist;
	}
	
	/*
	 * This will process the where statement, and output a string array. The
	 * first index of the array is the actual where statement to use, and the
	 * rest is for the whereArgs array. Right now, this only checks for AND, or
	 * OR, but this could be expanded in the future.
	 */
	private static String[] processWhere(String whereStatement) {
		String state = whereStatement;
		TwinList<String, String> statements = new TwinList<String, String>();
		// Cycle through the array of operators to look for, and add any of
		// their statements to the twinlist to be scanned, and put into an
		// array
		for (String op : OPS) {
			if (state.contains(op)) {
				String[] args = state.split(op);
				for (String s : args) {
					statements.add(op, s);
				}
			}
		}
		if (statements.size() == 0) {
			statements.add(AND, state);
		}
		return processOperations(statements);
	}

	private static String[] processOperations(TwinList<String, String> operations) {
		// Create an array 1 larger than the operations list. The first index
		// returned is the processed where statement, the rest are the
		// whereArgs
		String[] results = new String[operations.size() + 1];
		results[0] = "";
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			String op = operations.getFirst(i);
			String stmt = operations.getSecond(i);
			for (int x = 0; x < OPERATORS.length; x++) {
				if (stmt.contains(OPERATORS[x])) {
					if (i != 0) {
						results[0] += " " + op + " ";
					}
					String s = checkColumnName(stmt.split(OP_ESCAPED[x])[0].trim()) + OPERATORS[x] + "?";
					results[0] += s;
					results[(i + 1)] = stmt.split(OP_ESCAPED[x])[1].trim();
				}
			}
		}
		return results;
	}

	/**
	 * 
	 * Removes a row from the specified table
	 * 
	 * @param table
	 *            The table to remove the row from
	 * @param id
	 *            The rowid to remove
	 */
	public void RemoveRow(String table1, long id) {
		String table = checkColumnName(table1);
		synchronized (dbLock) {
			SQLiteDatabase db = getDataBase();
			db.delete(table, "_id=?", new String[] { String.valueOf(id) });
		}
	}

	/**
	 * 
	 * @return If the db is locked by a thread, either current, or other
	 *         threads.
	 */
	@SuppressWarnings("deprecation")
	public boolean isLocked() {
		// dbCheck();
		SQLiteDatabase db = getDataBase();
		if (db.isDbLockedByCurrentThread() || db.isDbLockedByOtherThreads()) {
			return true;
		}
		return false;
	}

	/**
	 * Helper method to see if some data is actually in the table provided.
	 * Note: This is a heavy operation if you have a large database, as it scans
	 * the entire table. You're probably better off trying to get the rowId of
	 * the data, and if it returns -1, it's not there. However, this is a more
	 * broad search (searches the whole table rather than a column)
	 * 
	 * @param table
	 *            Table to check data in
	 * @param data
	 *            The data to check
	 * @return true if the data is in the database, false if not
	 */
	public boolean InTable(String table1, Object data) {

		if (!batch) {
			String table = checkColumnName(table1);
			String querystring = "select * from " + table;
			Cursor cursor;
			synchronized (dbLock) {
				SQLiteDatabase db = getDataBase();
				cursor = db.rawQuery(querystring, null);
			}
			int length = cursor.getColumnCount();
			if (cursor.moveToFirst()) {
				do {
					for (int i = 0; i < length; i++) {
						if (i == 0) {

						} else {
							if (cursor.getString(i).equals(data)) {
								return true;
							}
						}
					}
				} while (cursor.moveToNext());
			}
			cursor.close();
			return false;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	/**
	 * 
	 * @param table
	 *            The table to search
	 * @return an int of the amount of rows in the table. This may change to a
	 *         long at some point.
	 */
	public int GetRowCount(String table1) {
		if (!batch) {
			Cursor cursor;
			synchronized (dbLock) {
				SQLiteDatabase db = getDataBase();
				String table = checkColumnName(table1);
				cursor = db.rawQuery("select _id from " + table, null);
			}
			int count = cursor.getCount();
			cursor.close();
			return count;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	/**
	 * Grab a whole row of data from a table, of the specified rowid
	 * 
	 * @param table
	 *            The table where the data resides
	 * @param rowId
	 *            The rowid of the row you'd like to grab
	 * @return An ArrayList<String> of all the data in the specified table, and
	 *         rowid
	 */
	public ArrayList<String> GetRow(String table1, long rowId) {
		if (!batch) {
			ArrayList<String> list = new ArrayList<String>();
			Cursor cursor;
			synchronized (dbLock) {
				SQLiteDatabase db = getDataBase();
				String table = checkColumnName(table1);
				cursor = db.query(table, null, COLUMN_ID + "=" + rowId, null, null, null, null);
			}
			if (cursor.moveToFirst()) {
				int count = cursor.getColumnCount();
				for (int i = 0; i < count; i++) {
					if (i == 0) {
					} else {
						list.add(cursor.getString(i));
					}
				}
			}
			cursor.close();
			return list;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	/**
	 * Use this method to get the rowid of a piece of data. If the data is not
	 * found, -1 will be returned.
	 * 
	 * @param table
	 *            The table to search
	 * @param column
	 *            The column to search in
	 * @param data
	 *            The data to look for. This is case sensitive
	 * @return the rowid of the data (long)
	 */

	public long GetRowID(String table1, String column, String data) {
		if (!batch) {
			synchronized (dbLock) {				
				return getRowId(getDataBase(), table1, column, data);
			}
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	private static long getRowId(SQLiteDatabase db, String table, String column, String data) {
		long id = -1;
		Cursor cursor = db.rawQuery("SELECT _id FROM " + checkTableName(table) + " WHERE " + checkColumnName(column) + "=? LIMIT 1",
				new String[] { data });
		if (cursor.moveToFirst()) {
			id = cursor.getLong(0);
		}
		cursor.close();
		return id;
	}

	/**
	 * Use this method to get a single item from the database. This requires the
	 * rowid of the item. Use GetRowID to obtain the rowid.
	 * 
	 * @param table
	 *            The table the value resides in
	 * @param columnName
	 *            The column the value resides in
	 * @param id
	 *            the rowid of the data
	 * @return a String of the data stored in the specified position
	 */
	public String GetValue(String table1, String columnName1, long id) {
		if (!batch) {
			String s;
			SQLiteDatabase db = getDataBase();			
			synchronized (dbLock) {
				s = getValue(db, table1, columnName1, id);
			}
			return new String(s);
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}

	private static String getValue(SQLiteDatabase db, String table, String columnName, long id) {
		String rtn;
		Cursor cursor;
		cursor = db.rawQuery("SELECT " + checkColumnName(columnName) + " FROM " + checkTableName(table) + " WHERE " + COLUMN_ID + "=?",
				new String[] { id + "" });
		if (cursor.moveToFirst()) {
			rtn = cursor.getString(0);
		} else {
			Log.e(TAG, "List is empty. Are you sure you have a valid rowid?");
			rtn = "";
		}
		cursor.close();
		return rtn;
	}

	/**
	 * Get all data in a column. This will return an empty list if there is no
	 * data in the column. An error will also be sent into logcat.
	 * 
	 * @param table
	 *            The table of the column you want returned
	 * @param column
	 *            The column you'd like returned
	 * @return an ArrayList<String> of all the values in the column.
	 */
	public ArrayList<String> GetColumn(String table1, String column1) {
		if (!batch) {
			SQLiteDatabase db = getDataBase();
			ArrayList<String> list;
			synchronized (dbLock) {
				list = getColumn(db, table1, column1);
			}			
			return list;
		}
		throw new SimpleSQLBatchException("SimpleSQL received a query call while in BatchWrite mode!");
	}
	
	private static ArrayList<String> getColumn(SQLiteDatabase db, String table1, String column1) {
		ArrayList<String> list = new ArrayList<String>();		
		Cursor cursor = db.rawQuery("SELECT " + checkColumnName(column1) + " FROM " + checkTableName(table1), null);
		if (cursor.moveToFirst()) {
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return list;
	}

	private void genDatabaseCreateStmt(int table, int cnt, DBBuilder builder) {
		String tbl = TABLE_NAME.trim();
		tbl = checkTableName(tbl);
		if (cnt == 1) {
			String column = checkColumnName(builder.Column(table)[0]);
			DATABASE_CREATE = " create table if not exists " + tbl + " (" + COLUMN_ID + " integer primary key autoincrement, " + column + " text";
			int cols = builder.ColumnCount(TABLE_NAME);
			for (int i = 1; i < cols; i++) {
				column = checkColumnName(builder.Column(table)[i]);
				DATABASE_CREATE = DATABASE_CREATE + ", " + column + " text";
			}
			DATABASE_CREATE = DATABASE_CREATE + ")";
		} else {
			String column = checkColumnName(builder.Column(table)[0]);
			DATABASE_CREATE = " create table if not exists " + tbl + " (" + COLUMN_ID + " integer primary key autoincrement, " + column + " text";
			int cols = builder.ColumnCount(TABLE_NAME);
			for (int i = 1; i < cols; i++) {
				column = checkColumnName(builder.Column(table)[i]);
				DATABASE_CREATE = DATABASE_CREATE + ", " + column + " text";
			}
			DATABASE_CREATE = DATABASE_CREATE + ")";
		}
	}

	/**
	 * This method will wrap the string in square brackets. This will first
	 * strip any, if they are in the string.
	 * 
	 * @param name
	 * @return
	 */
	public static String checkTableName(String name) {
		String n = name.trim();
		n = n.replace("'", "");
		n = "'" + n + "'";				
		return n;
	}
	
	public static String checkColumnName(String name) {
		if (name.startsWith("\"") && name.endsWith("\"")) {
			return name;
		} else {
			return "\"" + name + "\"";
		}
	}

	/**
	 * This is for advanced users. This returns the lock that is used to
	 * synchronize db calls. If you need to do the synchronizing yourself, you
	 * should get this lock, and synchronize with it to prevent concurrent
	 * operations from occuring.
	 * 
	 * @return - The lock used to synchronize database calls.
	 */
	public Object getLock() {
		return dbLock;
	}

	public static void clearMemory() {		
		SQLiteDatabase.releaseMemory();
	}
	
	/**
	 * This resets the database by closing it, then re-opening it. Then,
	 * the releaseMemory() method is run, along with System.gc().
	 */
	public void clearMemoryReset() {
		resetDB();
		SQLiteDatabase.releaseMemory();
		System.gc();
	}

	/**
	 * If this is set to true, the db will not be cleared from memory when the
	 * form loses focus. It will still get cleared from memory if the form is
	 * destroyed, however.
	 * 
	 * @param enabled
	 */
	public void FreeRunning(boolean enabled) {
		dontStop = true;
	}

	/**
	 * 
	 * @return The DBBuilder associated with this database. This can change over
	 *         time, and may not match what you originally assigned to the
	 *         builder.
	 */
	public DBBuilder getBuilder() {
		return dBuilder;
	}

	/**
	 * 
	 * @return Whether the db is in FreeRunning mode or not.
	 */
	public boolean FreeRunning() {
		return dontStop;
	}

	public boolean isInBatchMode() {
		return batch;
	}

	public Form getParentForm() {
		if (container != null) {
			if (container.$context() instanceof Form) {
				return (Form) container.$context();
			} else {
				Log.e("SimpleSQL", "Parent Form is not a Form (most likely due to being a service, or FormFragment.");
				return null;
			}
		} else {
			return null;
		}
	}

	public FormService getParentFormService() {
		if (sContainer != null) {
			return sContainer.$formService();
		} else {
			return null;
		}
	}

	public void addTableToExistingDB(String tableName, String[] columns) {
		String[] dt = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			dt[i] = "text";
		}
		dBuilder.addTable(tableName, columns, dt);
		dBuilder.Version(dBuilder.Version() + 1);
		DATABASE_VERSION = dBuilder.Version();
		addedTableNames.add(tableName);
		storage.StoreValue(BUILDER_SAVE, dBuilder);
		addingTable = true;
		resetDB();
	}
	
	
	public void saveBuilder(DBBuilder builder) {
		DATABASE_VERSION = builder.Version();
		DATABASE_NAME = builder.DBName();
		BUILDER_SAVE = getBuilderSaveName();
		storage.StoreValue(BUILDER_SAVE, builder);
	}

	/**
	 * Adds a column to an existing table in a database.
	 * 
	 * @param table
	 *            - The table to add the column to
	 * @param columnName
	 *            - The name of the new column
	 */
	public boolean addColumn(String table, String columnName) {
		if (dBuilder.TableId(table) == -1) {
			dBuilder.addColumn(table, columnName);
			if (sDb != null) {
				sDb.execSQL("ALTER TABLE " + checkColumnName(table) + " ADD COLUMN " + checkColumnName(columnName));
			} else {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.execSQL("ALTER TABLE " + checkColumnName(table) + " ADD COLUMN " + checkColumnName(columnName));
			}
			storage.StoreValue(BUILDER_SAVE, dBuilder);
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a column to an existing table in a database.
	 * 
	 * @param table
	 *            - The table to add the column to
	 * @param columnName
	 *            - The name of the new column
	 * @param force
	 * 			  - If true, it will try to add the column to the table, even if it already exists in the DBBuilder. This will
	 * cause an exception to be thrown if the column already exists in the database. Use with caution!
	 */
	public boolean addColumn(String table, String columnName, boolean force) {
		if (force || dBuilder.TableId(table) == -1) {
			if (dBuilder.TableId(table) == -1) {
				dBuilder.addColumn(table, columnName);
			}
			if (sDb != null) {
				sDb.execSQL("ALTER TABLE " + checkColumnName(table) + " ADD COLUMN " + checkColumnName(columnName));
			} else {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.execSQL("ALTER TABLE " + checkColumnName(table) + " ADD COLUMN " + checkColumnName(columnName));
			}
			storage.StoreValue(BUILDER_SAVE, dBuilder);
			return true;
		}
		return false;
	}

	public boolean columnExists(String table, String columnName) {
		if (sDb != null) {
			Cursor cursor = sDb.rawQuery("SELECT * FROM " + fixTableName(table) + " LIMIT 0, 1", null);
			int i = cursor.getColumnIndex(columnName);
			if (i != -1) {
				// Check the builder to make sure it has this column listed, as it's in the db
				int pos = dBuilder.ColumnPosition(table, columnName);
				if (pos == -1) {
					dBuilder.addColumn(table, columnName);
					saveBuilder(dBuilder);
				}
			}
			return i != -1;
		} else {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + fixTableName(table) + " LIMIT 0, 1", null);
			int i = cursor.getColumnIndex(columnName);
			if (i != -1) {
				// Check the builder to make sure it has this column listed, as it's in the db
				int pos = dBuilder.ColumnPosition(table, columnName);
				if (pos == -1) {
					dBuilder.addColumn(table, columnName);
					saveBuilder(dBuilder);
				}
			}
			return i != -1;
		}
	}

	/**
	 * Use this method if you need to rename a table
	 * 
	 * @param oldTable
	 *            - The old table to rename
	 * @param newTable
	 *            - The new table name
	 * @return - true if the oldtable name existed, not necessarily if the
	 *         rename succeeded.
	 */
	public void renameTable(String oldTable, String newTable) {
		if (dBuilder.containsTable(oldTable)) {			
			if (sDb != null) {
				sDb.execSQL("ALTER TABLE " + checkTableName(oldTable) + " RENAME TO " + checkTableName(newTable));
				dBuilder = getBuilderInfo(sDb, dBuilder.DBName());
			} else {
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				db.execSQL("ALTER TABLE " + checkTableName(oldTable) + " RENAME TO " + checkTableName(newTable));
				dBuilder = getBuilderInfo(db, dBuilder.DBName());
			}
			storage.StoreValue(BUILDER_SAVE, dBuilder);
		} else {
			Log.e(TAG, "OldTable name " + oldTable + "doesn't exist!");
		}
	}

	/**
	 * This will delete the entire database (tables and columns and all). The db
	 * will then be rebuilt with the supplied builder.
	 * 
	 * @param builder
	 */
	public void ClearDB(DBBuilder builder) {
		dBuilder = builder;
		DATABASE_VERSION = dBuilder.Version();
		boolean s = false;
		if (sDb != null) {
			s = true;
			sDb.close();
		} else {
			dbHelper.getWritableDatabase().close();
		}
		dbHelper.close();
		getContext().deleteDatabase(DATABASE_NAME);
		BUILDER_SAVE = getBuilderSaveName();
		storage.StoreValue(BUILDER_SAVE, builder);
		dbHelper = new BigDBSqlOpenHelper(getContext());
		if (s) {
			sDb = dbHelper.getWritableDatabase();
		} else {
			dbHelper.getWritableDatabase();
		}
	}
		
	public void rescanForBuilder() {
		String dbName = dBuilder.DBName();
		dBuilder = getBuilderInfo(getDataBase(), dbName);
		BUILDER_SAVE = getBuilderSaveName();
		storage.StoreValue(BUILDER_SAVE, dBuilder);
	}

	private void resetDB() {
		SQLiteDatabase db;
		boolean b = false;
		if (sDb == null) {
			db = dbHelper.getWritableDatabase();
			b = true;
		} else {
			db = sDb;
		}
		db.close();
		dbHelper.close();
		dbHelper = new BigDBSqlOpenHelper(getContext());
		if (!b) {
			sDb = dbHelper.getWritableDatabase();
		} else {
			db = dbHelper.getWritableDatabase();
		}
	}
		
	private SQLiteDatabase getDataBase() {
		if (sDb == null) {
			return dbHelper.getWritableDatabase();
		} else {
			return sDb;
		}
	}
	
	@Override
	public void onResume() {
		if (keepInMem) {
			sDb = dbHelper.getWritableDatabase();
		}
	}

	@Override
	public void onStop() {
		if (!dontStop) {
			if (batch) {
				synchronized (dbLock) {
					if (sDb == null) {
						try {
							SQLiteDatabase db = dbHelper.getWritableDatabase();
							db.setTransactionSuccessful();
							db.endTransaction();
						} catch (IllegalStateException e) {
							// not in batch mode, it's ok
						}
					} else {
						try {
							sDb.setTransactionSuccessful();
							sDb.endTransaction();
						} catch (IllegalStateException e) {
							// not in batch mode, it's ok
						}
					}
				}
			}
			synchronized (dbLock) {
				if (sDb == null) {
					SQLiteDatabase db = dbHelper.getWritableDatabase();
					if (db != null) {
						db.close();
						SQLiteDatabase.releaseMemory();
						db = null;
					}
				} else {
					sDb.close();
					SQLiteDatabase.releaseMemory();
				}
			}
		}
	}

	private class BigDBSqlOpenHelper extends SQLiteOpenHelper {

		public BigDBSqlOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			if (dBuilder == null) {
				throw new RuntimeException("Table name list is empty! Can't create database.");
			}
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			int cnt = dBuilder.TableCount();
			for (int i = 0; i < cnt; i++) {
				TABLE_NAME = dBuilder.Table(i);
				genDatabaseCreateStmt(i, cnt, dBuilder);
				db.execSQL(DATABASE_CREATE);				
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (upgradeIntf != null) {
				upgradeIntf.onUpgrade(db, oldVersion, newVersion);
			} else {
				if (addingTable) {
					for (String tbl : addedTableNames) {
						int tblid = dBuilder.TableId(tbl);
						TABLE_NAME = dBuilder.Table(tblid);
						genDatabaseCreateStmt(tblid, 1, dBuilder);
						db.execSQL(DATABASE_CREATE);
						addedTableNames.remove(tbl);
					}
					addingTable = false;
				} else {
					Log.w(BigDBSqlOpenHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion + ".");
					int tblcnt = dBuilder.TableCount();
					// TODO - Right now this is a destructive upgrade. It wipes
					// the old structure, and creates the new, destroying old data in
					// the process. Is it even necessary now? The main reason to upgrade the
					// db is to add a table, which is taken care of above.
					for (int i = 0; i < tblcnt; i++) {
						db.execSQL("DROP TABLE IF EXISTS " + checkTableName(dBuilder.Table(i)));
					}
					onCreate(db);
				}
			}
		}
	}

	@Override
	public void onDestroy() {
		// Hopefully this should never be true, but it's there as a convenience
		// so it doesn't throw an error.
		SQLiteDatabase db = getDataBase();
		if (batch) {
			db.setTransactionSuccessful();
			db.endTransaction();
		}
		SQLiteDatabase.releaseMemory();
		db.close();
		db = null;
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

	public interface OnSQLUpgrade {
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}

}
