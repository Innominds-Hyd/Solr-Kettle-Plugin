package com.innominds.kettle.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Counter;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.w3c.dom.Node;

public class SolrOutMeta extends BaseStepMeta
implements StepMetaInterface
{

	private static Class<?> PKG = SolrOutMeta.class;
	private String url;
	
	HttpSolrServer server;
	
	private String collection;
	private SolrInputDocument thisDoc = new SolrInputDocument();
	
	public void setDefault()
	{
		this.url  = "http://localhost:8983/solr";
		this.collection = "collection1";
	}

	public String getXML() throws KettleValueException
	{
		StringBuilder retval = new StringBuilder();
		retval.append("    ").append(XMLHandler.addTagValue("url", this.url));
		retval.append("    ").append(XMLHandler.addTagValue("collection", this.collection));
		return retval.toString();
	}

	public void loadXML(Node stepnode, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleXMLException
			{
		setUrl(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "url")));
		setCollection(XMLHandler.getNodeValue(XMLHandler.getSubNode(stepnode, "collection")));
			}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	public String getCollection() {
		return this.collection;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}

	public void saveRep(Repository rep, ObjectId id_transformation, ObjectId id_step)
			throws KettleException
			{
		rep.saveStepAttribute(id_transformation, id_step, "url", this.url);
		rep.saveStepAttribute(id_transformation, id_step, "collection", this.collection);
			}

	public void readRep(Repository rep, ObjectId id_step, List<DatabaseMeta> databases, Map<String, Counter> counters)
			throws KettleException
			{
		this.url = rep.getStepAttributeString(id_step, "url");
		this.collection = rep.getStepAttributeString(id_step, "collection");
			}

	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output, RowMetaInterface info)
	{
		CheckResult cr = new CheckResult(1, BaseMessages.getString(PKG, 
				"ExcelInputMeta.CheckResult.AcceptFilenamesOk", new String[0]), stepMeta);
		remarks.add(cr);
	}

	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta, Trans trans)
	{
		return new SolrOutput(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public StepDataInterface getStepData()
	{
		return new SolrOutData();
	}

	public void loadData(String row)
	{
		SolrServer server = new HttpSolrServer(url+"/"+collection);
		((HttpSolrServer) server).setParser(new XMLResponseParser());
		
		logBasic("Loading data into Solr started with Url: "+ url);
		String[] field = row.split("\n");
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		
		for(int i =0; i < field.length; i++)
		{
			thisDoc = new SolrInputDocument();
			String[] index_data = field[i].split("\t");
			for(int j = 0; j < index_data.length; j++)
			{
				String[] index_fields = index_data[j].split(",");
				log.logBasic("Index field is " + index_fields[0] + " , Index Value is " + index_fields[1]);
				thisDoc.addField(index_fields[0],index_fields[1]);
			}
			docs.add(thisDoc);
		}
		
		try {
			log.logBasic("Came to load Docs");
			server.add(docs);
			server.commit();
			log.logBasic("Docs are loaded Successfully");
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logBasic("Solr Loading is Completed...");
	}


}
