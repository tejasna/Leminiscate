package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Login;
import javax.inject.Inject;
import javax.inject.Singleton;

import static com.leminiscate.utils.PreConditions.checkNotNull;

@Singleton public class WalletRepository implements WalletDataSource {

  private final WalletDataSource mTasksRemoteDataSource;

  private final WalletDataSource mTasksLocalDataSource;

  /**
   * This variable has package local visibility so it can be accessed from tests.
   */
  //Map<String, Task> mCachedTasks;

  /**
   * Marks the cache as invalid, to force an update the next time data is requested. This variable
   * has package local visibility so it can be accessed from tests.
   */
  boolean mCacheIsDirty = false;

  /**
   * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
   * required to create an instance of the TasksRepository. Because {@link "TasksDataSource} is an
   * interface, we must provide to Dagger a way to build those arguments, this is done in
   * {@link "TasksRepositoryModule}.
   * <P>
   * When two arguments or more have the same type, we must provide to Dagger a way to
   * differentiate them. This is done using a qualifier.
   * <p>
   * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
   * with {@code @Nullable} values.
   */
  @Inject WalletRepository(@Remote WalletDataSource tasksRemoteDataSource,
      @Local WalletDataSource tasksLocalDataSource) {
    mTasksRemoteDataSource = tasksRemoteDataSource;
    mTasksLocalDataSource = tasksLocalDataSource;
  }

  /**
   * Gets tasks from cache, local data source (SQLite) or remote data source, whichever is
   * available first.
   * <p>
   * Note: {@link "LoadTasksCallback#onDataNotAvailable()} is fired if all data sources fail to
   * get the data.
   */
  @Override public void getTasks(@NonNull final LoadTasksCallback callback) {
    checkNotNull(callback);

    // Respond immediately with cache if available and not dirty
    //if (mCachedTasks != null && !mCacheIsDirty) {
    //  callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
    //  return;
    //}
    //
    //if (mCacheIsDirty) {
    //  // If the cache is dirty we need to fetch new data from the network.
    //  getTasksFromRemoteDataSource(callback);
    //} else {
    //  // Query the local storage if available. If not, query the network.
    //  mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
    //    @Override public void onTasksLoaded(List<Task> tasks) {
    //      refreshCache(tasks);
    //      callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
    //    }
    //
    //    @Override public void onDataNotAvailable() {
    //      getTasksFromRemoteDataSource(callback);
    //    }
    //  });
    //}
  }

  //@Override public void saveTask(@NonNull Task task) {
  //  checkNotNull(task);
  //  mTasksRemoteDataSource.saveTask(task);
  //  mTasksLocalDataSource.saveTask(task);
  //
  //  // Do in memory cache update to keep the app UI up to date
  //  if (mCachedTasks == null) {
  //    mCachedTasks = new LinkedHashMap<>();
  //  }
  //  mCachedTasks.put(task.getId(), task);
  //}
  //
  //@Override public void completeTask(@NonNull Task task) {
  //  checkNotNull(task);
  //  mTasksRemoteDataSource.completeTask(task);
  //  mTasksLocalDataSource.completeTask(task);
  //
  //  Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
  //
  //  // Do in memory cache update to keep the app UI up to date
  //  if (mCachedTasks == null) {
  //    mCachedTasks = new LinkedHashMap<>();
  //  }
  //  mCachedTasks.put(task.getId(), completedTask);
  //}

  @Override public void completeTask(@NonNull String taskId) {
    checkNotNull(taskId);
    // completeTask(getTaskWithId(taskId));
  }

  //@Override public void activateTask(@NonNull Task task) {
  //  checkNotNull(task);
  //  mTasksRemoteDataSource.activateTask(task);
  //  mTasksLocalDataSource.activateTask(task);
  //
  //  Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
  //
  //  // Do in memory cache update to keep the app UI up to date
  //  if (mCachedTasks == null) {
  //    mCachedTasks = new LinkedHashMap<>();
  //  }
  //  mCachedTasks.put(task.getId(), activeTask);
  //}

  @Override public void activateTask(@NonNull String taskId) {
    checkNotNull(taskId);
    //activateTask(getTaskWithId(taskId));
  }

  @Override public void clearCompletedTasks() {
    mTasksRemoteDataSource.clearCompletedTasks();
    mTasksLocalDataSource.clearCompletedTasks();

    // Do in memory cache update to keep the app UI up to date
    //if (mCachedTasks == null) {
    //  mCachedTasks = new LinkedHashMap<>();
    //}
    //Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
    //while (it.hasNext()) {
    //  Map.Entry<String, Task> entry = it.next();
    //  if (entry.getValue().isCompleted()) {
    //    it.remove();
    //  }
    //}
  }

  /**
   * Gets tasks from local data source (sqlite) unless the table is new or empty. In that case it
   * uses the network data source. This is done to simplify the sample.
   * <p>
   * Note: {@link "LoadTasksCallback#onDataNotAvailable()} is fired if both data sources fail to
   * get the data.
   */
  @Override public void getTask(@NonNull final String taskId,
      @NonNull final GetTaskCallback callback) {
    checkNotNull(taskId);
    checkNotNull(callback);

    //Task cachedTask = getTaskWithId(taskId);
    //
    //// Respond immediately with cache if available
    //if (cachedTask != null) {
    //  callback.onTaskLoaded(cachedTask);
    //  return;
    //}
    //
    //// Load from server/persisted if needed.
    //
    //// Is the task in the local data source? If not, query the network.
    //mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
    //  @Override public void onTaskLoaded(Task task) {
    //    callback.onTaskLoaded(task);
    //  }
    //
    //  @Override public void onDataNotAvailable() {
    //    mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
    //      @Override public void onTaskLoaded(Task task) {
    //        callback.onTaskLoaded(task);
    //      }
    //
    //      @Override public void onDataNotAvailable() {
    //        callback.onDataNotAvailable();
    //      }
    //    });
    //  }
    //});
  }

  @Override public void refreshTasks() {
    mCacheIsDirty = true;
  }

  @Override public void deleteAllTasks() {
    mTasksRemoteDataSource.deleteAllTasks();
    mTasksLocalDataSource.deleteAllTasks();

    //if (mCachedTasks == null) {
    //  mCachedTasks = new LinkedHashMap<>();
    //}
    //mCachedTasks.clear();
  }

  @Override public void deleteTask(@NonNull String taskId) {
    mTasksRemoteDataSource.deleteTask(checkNotNull(taskId));
    mTasksLocalDataSource.deleteTask(checkNotNull(taskId));

    //mCachedTasks.remove(taskId);
  }

  @Override public void login(@NonNull LoginCallback callback) {

    mTasksLocalDataSource.checkLoginState(new LoginCallback() {
      @Override public void userExists() {
        callback.userExists();
      }

      @Override public void onLoginSuccess(Login login) {
        callback.onLoginSuccess(login);
      }

      @Override public void onLoginFailure() {
        mTasksRemoteDataSource.login(callback);
      }
    });
  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {

  }

  @Override public void saveLoginState(@NonNull Login login) {
    mTasksLocalDataSource.saveLoginState(login);
  }

  @Override public void clearLoginState() {
    mTasksLocalDataSource.clearLoginState();
  }

  @Override public void clearSubscriptions() {
    mTasksRemoteDataSource.clearSubscriptions();
  }

  private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
    //mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
    //  @Override public void onTasksLoaded(List<Task> tasks) {
    //    refreshCache(tasks);
    //    refreshLocalDataSource(tasks);
    //    callback.onTasksLoaded(new ArrayList<>(mCachedTasks.values()));
    //  }
    //
    //  @Override public void onDataNotAvailable() {
    //    callback.onDataNotAvailable();
    //  }
    //});
  }

  // private void refreshCache(List<Task> tasks) {
  //if (mCachedTasks == null) {
  //  mCachedTasks = new LinkedHashMap<>();
  //}
  //mCachedTasks.clear();
  //for (Task task : tasks) {
  //  mCachedTasks.put(task.getId(), task);
  //}
  // mCacheIsDirty=false;
}

// private void refreshLocalDataSource(List<Task> tasks) {
//mTasksLocalDataSource.deleteAllTasks();
//for (Task task : tasks) {
//  mTasksLocalDataSource.saveTask(task);
//}
// }

//@Nullable private Task getTaskWithId(@NonNull String id) {
//  checkNotNull(id);
//  if (mCachedTasks == null || mCachedTasks.isEmpty()) {
//    return null;
//  } else {
//    return mCachedTasks.get(id);
//  }
//}
