module org.openjfx.gradefx {
    requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires transitive org.openjfx.kafx;
	
    exports org.openjfx.gradefx.view to javafx.graphics;
}
