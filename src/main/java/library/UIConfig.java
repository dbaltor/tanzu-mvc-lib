package library;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor(staticName = "of")
public class UIConfig {

    private final boolean tanzu;
    private final int bgColor;
    
}