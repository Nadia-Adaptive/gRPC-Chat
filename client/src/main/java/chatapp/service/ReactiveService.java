package chatapp.service;

import java.util.function.Consumer;

public interface ReactiveService<T> {

    void setErrorHandler(Consumer<Throwable> t);

    void setSuccessHandler(Consumer<T> t);

    void closeService();
}
