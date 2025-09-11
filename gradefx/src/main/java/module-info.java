module org.openjfx.gradefx {
    requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.graphics;
	requires transitive org.openjfx.kafx;
//	requires org.apache.pdfbox;
	
    exports org.openjfx.gradefx.view to javafx.graphics;
}
