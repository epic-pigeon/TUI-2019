package Problem_4;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;

public class AsyncImage extends Image {
    private BooleanProperty loaded = new SimpleBooleanProperty(false);

    {
        progressProperty().addListener((observable, oldValue, progress) -> {
            if ((Double) progress == 1.0) {
                loaded.setValue(true);
            }
        });
    }

    public AsyncImage(String url) {
        super(url, true);
    }

    public AsyncImage(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth) {
        super(url, requestedWidth, requestedHeight, preserveRatio, smooth);
    }

    public AsyncImage(String url, double requestedWidth, double requestedHeight, boolean preserveRatio, boolean smooth, boolean backgroundLoading) {
        super(url, requestedWidth, requestedHeight, preserveRatio, smooth, true);
    }

    public void onLoad(Runnable runnable, boolean invokeIfLoaded) {
        if (invokeIfLoaded && loaded.get()) runnable.run();
        loaded.addListener((observable, oldValue, newValue) -> {
            if (newValue) runnable.run();
        });
    }

    public void onLoad(Runnable runnable) {
        onLoad(runnable, true);
    }
}
