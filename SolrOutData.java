package com.innominds.kettle.solr;

import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;

public class SolrOutData extends BaseStepData implements StepDataInterface{
	protected 	RowMetaInterface outputRowMeta;
	public 		RowMetaInterface inputRowMeta;
	
	
	public RowMetaInterface getOutputRowMeta() {
		return outputRowMeta;
	  }
	
	public void setOutputRowMeta(RowMetaInterface rmi) {
	    outputRowMeta = rmi;
	  }
	
	public SolrOutData()
	  {
	    
	  }

}
