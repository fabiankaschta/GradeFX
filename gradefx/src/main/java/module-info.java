module org.openjfx.gradefx {
    requires transitive javafx.controls;
	requires transitive org.openjfx.kafx;
	
    exports org.openjfx.gradefx.view to javafx.graphics;
}
