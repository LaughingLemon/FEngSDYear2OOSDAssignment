package bromley.bopak3.client.components;
//Created by Shaun

import java.awt.*;

public class LEDDisplayPanel extends Panel {
    protected LayoutManager createLayout(int segments) {
        return new GridLayout(1, segments);
    }
}
