package IDSmain;

import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.table.AbstractTableModel;


public class IntruderTable extends AbstractTableModel  {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Vector<Intruder> intruderList = new Vector<Intruder>();
    protected EventListenerList listenerList = new EventListenerList();
	
    final public static String[] columnNames = {"Num", "Time", "Source IP", "Destination IP", "Attack type"};
	final public static Object[] longValues = { 1000, "12:00:00.000 Jan 01, 2000 ", "255.255.255.255",	"255.255.255.255", "DOS"};
    public synchronized void addIntruder(Intruder pIntruder){
    	intruderList.addElement(pIntruder);
//    	fireTableStructureChanged();
    	fireTableDataChanged();
//    	System.out.println("New table");
    }
    
       
    
    public int getRowCount(){
        return intruderList.size();
    }
    
	public int getColumnCount() {
		return columnNames.length;
	}
    
    
    public Object getValueAt(int rowIndex, int columnIndex){
        return ((Intruder) intruderList.get(rowIndex)).getRowData(columnIndex);
    }
    
	public String getColumnName(int i) { return columnNames[i]; }


    
	public IntruderTable() {
	}


	public void clear() {
		intruderList.clear();
		fireTableDataChanged();
	}



	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return false;
	}




	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}

