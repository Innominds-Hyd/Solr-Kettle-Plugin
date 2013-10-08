package com.innominds.kettle.solr;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

public class SolrOutput extends BaseStep
implements StepInterface
{
	private SolrOutData data;
	private SolrOutMeta meta;
	String rowData = "";

	String path = getClass().getClassLoader().getResource(".").getPath();

	public SolrOutput(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans)
	{
		super(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public boolean processRow(StepMetaInterface smi, StepDataInterface sdi) throws KettleException
	{
		this.meta = ((SolrOutMeta)smi);
		this.data = ((SolrOutData)sdi);

		Object[] row = getRow();

		if (row == null)
		{
			meta.loadData(rowData);
			setOutputDone();
			return false;
		}

		RowMetaInterface rowMeta = getInputRowMeta();
		String[] fields = rowMeta.getFieldNames();

		if (this.first)
		{
			this.data.outputRowMeta = new RowMeta();
			this.meta.getFields(this.data.outputRowMeta, getStepname(), null, null, this);
		}

		for (int i = 0; i < row.length; i++)
		{
			if(row[i] !=  null)
			rowData = rowData + fields[i] + "," + row[i].toString() + "\t"; 
		}
		
		rowData = rowData + "\n";
		log.logBasic("String after concatanating is " + rowData);
		putRow(this.data.outputRowMeta, row);
		
		
		if (checkFeedback(getLinesRead())) {
			logBasic("Linenr " + getLinesRead());
		}

		if (checkFeedback(getLinesRead())) {
			logBasic("Linenr " + getLinesRead());
		}
		return true;
	}

	public boolean init(StepMetaInterface smi, StepDataInterface sdi) {
		this.meta = ((SolrOutMeta)smi);
		this.data = ((SolrOutData)sdi);

		return super.init(smi, sdi);
	}

	public void dispose(StepMetaInterface smi, StepDataInterface sdi) {
		this.meta = ((SolrOutMeta)smi);
		this.data = ((SolrOutData)sdi);
		super.dispose(smi, sdi);
	}

	public void run()
	{
		logBasic("Starting atload to run...");
		try {
			do if (!processRow(this.meta, this.data)) break; while (!isStopped());
		}
		catch (Exception e)
		{
			logError("Unexpected error : " + e.toString());
			logError(Const.getStackTracker(e));
			setErrors(1L);
			stopAll();
		} finally {
			dispose(this.meta, this.data);
			logBasic("Finished, processing " + getLinesRead() + " rows");
			markStop();
		}
	}
}
