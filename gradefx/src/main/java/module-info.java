module org.openjfx.gradefx {
    requires jdk.localedata;
    requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires transitive org.openjfx.kafx;
	requires transitive org.apache.pdfbox;
	requires com.github.zafarkhaja.semver;
	
    exports org.openjfx.gradefx.view to javafx.graphics;
    
    exports org.openjfx.gradefx.model;
    exports org.openjfx.gradefx.view.menu;
    exports org.openjfx.gradefx.view.pane;
    exports org.openjfx.gradefx.view.tab;
}
