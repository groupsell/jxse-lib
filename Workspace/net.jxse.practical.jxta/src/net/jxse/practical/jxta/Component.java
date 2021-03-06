package net.jxse.practical.jxta;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import net.jxse.osgi.message.IJxseMessagePrinter;
import net.jxse.osgi.message.IJxseMessagePrinter.MessageTypes;

public class Component implements CommandProvider{

	private static final String S_ASSIGNED_PRINTERS = "The following jxse printers are registered:\n";
	
	public static Collection<IJxseMessagePrinter> printers = new ArrayList<IJxseMessagePrinter>();
	
	public void activate(){ /*NOTHING */};
	public void deactivate(){ /*NOTHING */};
	
	public void addMessagePrinter( IJxseMessagePrinter printer ){
		printers.add( printer );
	}

	public void removeMessagePrinter( IJxseMessagePrinter printer ){
		printers.remove( printer );
	}
	
	/**
	 * Print the provided message. returns true if printers were found that accept the message,
	 * otherwise a false is returned
	 * @param type
	 * @param title
	 * @param message
	 * @return
	 */
	public static boolean printMessage( MessageTypes type, String title, String message ){
		if( printers.isEmpty() )
			return false;
		for( IJxseMessagePrinter printer: printers )
			printer.printMessage( type, title, message);
		return true;
	}

	/**
	 * Print the provided message. returns true if printers were found that accept the message,
	 * otherwise a false is returned
	 * @param title
	 * @param message
	 * @return
	 */
	public static int askQuestion( String title, String message ){
		if( printers.isEmpty() )
			return 0;
		int result = 0;
		for( IJxseMessagePrinter printer: printers ){
			result = printer.askQuestion( title, message);
			if( result != 0 )
				return result;
		}
		return result;
	}

	public Object _jxse_prt(CommandInterpreter ci) {
		StringBuffer buffer = new StringBuffer();
		buffer.append( S_ASSIGNED_PRINTERS );
		for( IJxseMessagePrinter printer: printers){
			buffer.append( printer.toString() + "\n");
		}
		buffer.append( "\n");
		return buffer.toString();
	}
	
	public String getHelp() {
		return "\tjxse_prt - Show the registered JXSE printers for practical JXTA II.";
	}
}
