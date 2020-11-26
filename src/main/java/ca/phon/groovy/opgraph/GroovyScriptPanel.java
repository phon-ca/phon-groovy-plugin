package ca.phon.groovy.opgraph;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.*;import ca.phon.ui.text.*;

public class GroovyScriptPanel extends JPanel {
	
	private final static String GROOVY_MIMETYPE = "text/groovy";
	
	/**
	 * Property name used when script text has been modified
	 * and focus has left the text component
	 */
	public final static String SCRIPT_UPDATED = "__script-updated__";

	private RSyntaxTextArea textArea;
	
	private String oldText = "";
	
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
		textArea.addFocusListener(new FocusListener() {
			
			@Override
			public void focusLost(FocusEvent e) {
				String newText = textArea.getText();
				if(!newText.equals(oldText)) {
					firePropertyChange(SCRIPT_UPDATED, oldText, newText);
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				oldText = textArea.getText();
			}
			
		});
		
		RTextScrollPane scroller = new RTextScrollPane(textArea);
		
		setLayout(new BorderLayout());
		add(scroller, BorderLayout.CENTER);
	}
	
	public RSyntaxTextArea getTextArea() {
		return this.textArea;
	}
	
	public String getScriptText() {
		return this.textArea.getText();
	}
	
	public void setScriptText(String text) {
		this.textArea.setText(text);
	}
	
}
