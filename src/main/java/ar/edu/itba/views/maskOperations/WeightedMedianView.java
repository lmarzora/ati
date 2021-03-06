package ar.edu.itba.views.maskOperations;

import ar.edu.itba.constants.FxmlEnum;
import ar.edu.itba.services.FxmlLoaderService;
import javafx.scene.layout.VBox;

import static ar.edu.itba.App.INJECTOR;

/**
 * Created by root on 8/30/17.
 */
public class WeightedMedianView extends VBox {
    public WeightedMedianView() {
        final FxmlLoaderService fxmlLoaderService = INJECTOR.getInstance(FxmlLoaderService.class);
        fxmlLoaderService.load(FxmlEnum.WEIGHTEDMEDIAN, this);
    }
}
