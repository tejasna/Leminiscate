package com.leminiscate.data.source.local;

import android.content.Context;
import android.support.annotation.NonNull;
import com.leminiscate.data.Login;
import com.leminiscate.data.source.WalletDataSource;
import io.realm.Realm;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.leminiscate.utils.PreConditions.checkNotNull;

@Singleton public class WalletLocalDataSource implements WalletDataSource {

  @Inject public WalletLocalDataSource(@NonNull Context context) {
    checkNotNull(context);
  }

  /**
   * Note: {@link LoadTasksCallback# onDataNotAvailable()} is fired if the database doesn't exist
   * or the table is empty.
   */
  @Override public void getTasks(@NonNull LoadTasksCallback callback) {
    //List<Task> tasks = new ArrayList<Task>();
    //SQLiteDatabase db = mDbHelper.getReadableDatabase();
    //
    //String[] projection = {
    //    TaskEntry.COLUMN_NAME_ENTRY_ID, TaskEntry.COLUMN_NAME_TITLE,
    //    TaskEntry.COLUMN_NAME_DESCRIPTION, TaskEntry.COLUMN_NAME_COMPLETED
    //};
    //
    //Cursor c = db.query(TaskEntry.TABLE_NAME, projection, null, null, null, null, null);
    //
    //if (c != null && c.getCount() > 0) {
    //  while (c.moveToNext()) {
    //    String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
    //    String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
    //    String description =
    //        c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
    //    boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
    //    Task task = new Task(title, description, itemId, completed);
    //    tasks.add(task);
    //  }
    //}
    //if (c != null) {
    //  c.close();
    //}
    //
    //db.close();
    //
    //if (tasks.isEmpty()) {
    //  // This will be called if the table is new or just empty.
    //  callback.onDataNotAvailable();
    //} else {
    //  callback.onTasksLoaded(tasks);
    //}
  }

  /**
   * Note: {@link GetTaskCallback# onDataNotAvailable()} is fired if the {@link } isn't
   * found.
   */
  @Override public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
    //SQLiteDatabase db = mDbHelper.getReadableDatabase();
    //
    //String[] projection = {
    //    TaskEntry.COLUMN_NAME_ENTRY_ID, TaskEntry.COLUMN_NAME_TITLE,
    //    TaskEntry.COLUMN_NAME_DESCRIPTION, TaskEntry.COLUMN_NAME_COMPLETED
    //};
    //
    //String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
    //String[] selectionArgs = { taskId };
    //
    //Cursor c =
    //    db.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
    //
    //Task task = null;
    //
    //if (c != null && c.getCount() > 0) {
    //  c.moveToFirst();
    //  String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
    //  String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
    //  String description = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
    //  boolean completed = c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
    //  task = new Task(title, description, itemId, completed);
    //}
    //if (c != null) {
    //  c.close();
    //}
    //
    //db.close();
    //
    //if (task != null) {
    //  callback.onTaskLoaded(task);
    //} else {
    //  callback.onDataNotAvailable();
    //}
  }

  //@Override public void saveTask(@NonNull Task task) {
  //  checkNotNull(task);
  //  SQLiteDatabase db = mDbHelper.getWritableDatabase();
  //
  //  ContentValues values = new ContentValues();
  //  values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
  //  values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
  //  values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
  //  values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());
  //
  //  db.insert(TaskEntry.TABLE_NAME, null, values);
  //
  //  db.close();
  //}

  //@Override public void completeTask(@NonNull Task task) {
  //  SQLiteDatabase db = mDbHelper.getWritableDatabase();
  //
  //  ContentValues values = new ContentValues();
  //  values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);
  //
  //  String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
  //  String[] selectionArgs = { task.getId() };
  //
  //  db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
  //
  //  db.close();
  //}

  @Override public void completeTask(@NonNull String taskId) {
    // Not required for the local data source because the {@link TasksRepository} handles
    // converting from a {@code taskId} to a {@link task} using its cached data.
  }

  //@Override public void activateTask(@NonNull Task task) {
  //  SQLiteDatabase db = mDbHelper.getWritableDatabase();
  //
  //  ContentValues values = new ContentValues();
  //  values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);
  //
  //  String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
  //  String[] selectionArgs = { task.getId() };
  //
  //  db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
  //
  //  db.close();
  //}

  @Override public void activateTask(@NonNull String taskId) {
    // Not required for the local data source because the {@link TasksRepository} handles
    // converting from a {@code taskId} to a {@link task} using its cached data.
  }

  @Override public void clearCompletedTasks() {
    //SQLiteDatabase db = mDbHelper.getWritableDatabase();
    //
    //String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
    //String[] selectionArgs = { "1" };
    //
    //db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    //
    //db.close();
  }

  @Override public void refreshTasks() {
    // Not required because the {@link TasksRepository} handles the logic of refreshing the
    // tasks from all the available data sources.
  }

  @Override public void deleteAllTasks() {
    //SQLiteDatabase db = mDbHelper.getWritableDatabase();
    //
    //db.delete(TaskEntry.TABLE_NAME, null, null);
    //
    //db.close();
  }

  @Override public void deleteTask(@NonNull String taskId) {
    //SQLiteDatabase db = mDbHelper.getWritableDatabase();
    //
    //String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
    //String[] selectionArgs = { taskId };
    //
    //db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    //
    //db.close();
  }

  @Override public void login(@NonNull LoginCallback callback) {

  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Login login = realm.where(Login.class).findFirst();
      if (login == null) {
        callback.onLoginFailure();
      } else {
        callback.userExists();
      }
    });
  }

  @Override public void saveLoginState(@NonNull Login login) {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      realm.copyToRealmOrUpdate(login);
      realm.close();
    });
  }

  @Override public void clearLoginState() {
    Realm.getDefaultInstance().executeTransaction(realm -> {
      Login login = realm.where(Login.class).findFirst();
      if (login != null) {
        login.deleteFromRealm();
      }
      realm.close();
    });
  }

  @Override public void clearSubscriptions() {
    // Not required for the local data source because the {@link WalletRepository} handles
    // clearing the composite disposable
  }
}