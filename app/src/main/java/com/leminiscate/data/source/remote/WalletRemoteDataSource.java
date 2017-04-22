package com.leminiscate.data.source.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import com.leminiscate.data.Login;
import com.leminiscate.data.source.NetModule;
import com.leminiscate.data.source.WalletDataSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.leminiscate.utils.PreConditions.checkNotNull;
import static io.reactivex.Observable.empty;

@Singleton public class WalletRemoteDataSource implements WalletDataSource {

  private final WalletApi restApi;

  private CompositeDisposable compositeSubscription;

  public WalletRemoteDataSource(@NonNull Context context) {
    checkNotNull(context);
    NetModule mNetModule = new NetModule();
    restApi = mNetModule.getRetrofit().create(WalletApi.class);
    compositeSubscription = new CompositeDisposable();
  }

  @Override public void login(final @NonNull LoginCallback callback) {
    compositeSubscription.add(restApi.login()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .doOnError(throwable -> {
          Timber.e(throwable.getMessage());
          callback.onLoginFailure();
        })
        .onErrorResumeNext(throwable -> {
          callback.onLoginFailure();
          return empty();
        })
        .subscribe(login -> {
          WalletRemoteDataSource.this.handleLoginResponse(login, callback);
        }));
  }

  @Override public void checkLoginState(@NonNull LoginCallback callback) {
    // Not required for the remote data source because the {@link TasksRepository} handles
  }

  @Override public void saveLoginState(@NonNull Login login) {
    // Not required for the remote data source because the {@link TasksRepository} handles
  }

  @Override public void clearLoginState() {
    // Not required for the remote data source because the {@link TasksRepository} handles
  }

  @Override public void clearSubscriptions() {
    compositeSubscription.clear();
  }

  private void handleLoginResponse(Login login, LoginCallback callback) {
    Timber.d(login.getToken());
    callback.onLoginSuccess(login);
  }

  //private static void addTask(String title, String description) {
  //  Task newTask = new Task(title, description);
  //  TASKS_SERVICE_DATA.put(newTask.getId(), newTask);
  //}

  /**
   * Note: {@link LoadTasksCallback# onDataNotAvailable()} is never fired. In a real remote data
   * source implementation, this would be fired if the server can't be contacted or the server
   * returns an error.
   */
  @Override public void getTasks(final @NonNull LoadTasksCallback callback) {
    // Simulate network by delaying the execution.
    //Handler handler = new Handler();
    //handler.postDelayed(new Runnable() {
    //  @Override
    //  public void run() {
    //    callback.onTasksLoaded(Lists.newArrayList(TASKS_SERVICE_DATA.values()));
    //  }
    //}, SERVICE_LATENCY_IN_MILLIS);
  }

  /**
   * Note: {@link "GetTaskCallback#onDataNotAvailable()} is never fired. In a real remote data
   * source implementation, this would be fired if the server can't be contacted or the server
   * returns an error.
   */
  @Override public void getTask(@NonNull String taskId, final @NonNull GetTaskCallback callback) {
    //final Task task = TASKS_SERVICE_DATA.get(taskId);
    //
    //// Simulate network by delaying the execution.
    //Handler handler = new Handler();
    //handler.postDelayed(new Runnable() {
    //  @Override
    //  public void run() {
    //    callback.onTaskLoaded(task);
    //  }
    //}, SERVICE_LATENCY_IN_MILLIS);
  }

  //@Override
  //public void saveTask(@NonNull Task task) {
  //  //TASKS_SERVICE_DATA.put(task.getId(), task);
  //}
  //
  //@Override
  //public void completeTask(@NonNull Task task) {
  //  Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
  //  TASKS_SERVICE_DATA.put(task.getId(), completedTask);
  //}

  @Override public void completeTask(@NonNull String taskId) {
    // Not required for the remote data source because the {@link TasksRepository} handles
    // converting from a {@code taskId} to a {@link task} using its cached data.
  }

  //@Override
  //public void activateTask(@NonNull Task task) {
  //  Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
  //  TASKS_SERVICE_DATA.put(task.getId(), activeTask);
  //}

  @Override public void activateTask(@NonNull String taskId) {
    // Not required for the remote data source because the {@link TasksRepository} handles
    // converting from a {@code taskId} to a {@link task} using its cached data.
  }

  @Override public void clearCompletedTasks() {
    //Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
    //while (it.hasNext()) {
    //  Map.Entry<String, Task> entry = it.next();
    //  if (entry.getValue().isCompleted()) {
    //    it.remove();
    //  }
    //}
  }

  @Override public void refreshTasks() {
    // Not required because the {@link TasksRepository} handles the logic of refreshing the
    // tasks from all the available data sources.
  }

  @Override public void deleteAllTasks() {
    //TASKS_SERVICE_DATA.clear();
  }

  @Override public void deleteTask(@NonNull String taskId) {
    //TASKS_SERVICE_DATA.remove(taskId);
  }
}