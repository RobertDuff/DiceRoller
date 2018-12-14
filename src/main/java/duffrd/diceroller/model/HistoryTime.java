package duffrd.diceroller.model;

import java.sql.Date;
import java.text.Format;
import java.text.SimpleDateFormat;

public class HistoryTime extends Date
{
	private static final Format FORMAT = new SimpleDateFormat ( "hh:mm:ss aa" );
	
	private static final long serialVersionUID = 1L;

	public HistoryTime()
	{
		this ( System.currentTimeMillis() );
	}
	
	public HistoryTime ( long date )
	{
		super ( date );
	}
	
	@Override
	public String toString()
	{
		return FORMAT.format ( this );
	}
}
