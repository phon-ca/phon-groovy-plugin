package ca.phon.groovy.opgraph;

import java.awt.*;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;

public class GroovyScriptPanel extends JPanel {
	
	private final static String GROOVY_MIMETYPE = "text/groovy";

	private RSyntaxTextArea textArea;
	
	public GroovyScriptPanel() {
		this("");
	}
	
	public GroovyScriptPanel(String script) {
		super();
		
		init();
		this.textArea.setText(script);
	}
	
	private void init() {
		textArea = new RSyntaxTextArea();
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
		
		RTextScrollPane scroller = new RTextScrollPane(textArea);
		
		setLayout(new BorderLayout());
		add(scroller, BorderLayout.CENTER);
	}
	
	public String getScriptText() {
		return this.textArea.getText();
	}
	
	public void setScriptText(String text) {
		this.textArea.setText(text);
	}
	
}
