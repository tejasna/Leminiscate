package com.leminiscate.data.source;

import android.support.annotation.NonNull;
import com.leminiscate.data.Login;

public interface WalletDataSource {

  interface LoadTasksCallback {

    //void onTasksLoaded(List<Task> tasks);
    //
    //void onDataNotAvailable();
  }

  interface GetTaskCallback {

    //void onTaskLoaded(Task task);
    //
    //void onDataNotAvailable();
  }

  interface LoginCallback {

    void userExists();

    void onLoginSuccess(Login login);

    void onLoginFailure();
  }

  void getTasks(@NonNull LoadTasksCallback callback);

  void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback);

  //void saveTask(@NonNull Task task);
  //
  //void completeTask(@NonNull Task task);

  void completeTask(@NonNull String taskId);

  //void activateTask(@NonNull Task task);

  void activateTask(@NonNull String taskId);

  void clearCompletedTasks();

  void refreshTasks();

  void deleteAllTasks();

  void deleteTask(@NonNull String taskId);

  void login(@NonNull LoginCallback callback);

  void checkLoginState(@NonNull LoginCallback callback);

  void saveLoginState(@NonNull Login login);

  void clearLoginState();

  void clearSubscriptions();
}
