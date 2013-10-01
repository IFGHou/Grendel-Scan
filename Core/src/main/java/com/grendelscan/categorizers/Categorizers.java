package com.grendelscan.categorizers;


import java.util.ArrayList;

import com.grendelscan.categorizers.impl.AllTransactionsCategorizer;
import com.grendelscan.categorizers.impl.ByBaseUriCategorizer;
import com.grendelscan.categorizers.impl.ByDirectoryCategorizer;
import com.grendelscan.categorizers.impl.ByFileCategorizer;
import com.grendelscan.categorizers.impl.ByHostCategorizer;
import com.grendelscan.categorizers.impl.ByHtmlElementCategorizer;
import com.grendelscan.categorizers.impl.ByHttpMethodCategorizer;
import com.grendelscan.categorizers.impl.ByRequestDataLocationCategorizer;
import com.grendelscan.categorizers.impl.ByHttpQueryCategorizer;
import com.grendelscan.categorizers.impl.ByHttpResponseCodeCategorizer;
import com.grendelscan.categorizers.impl.ByMimeTypeCategorizer;
import com.grendelscan.categorizers.impl.ByOutputContextCategorizer;
import com.grendelscan.categorizers.impl.ByQueryNamedDataCategorizer;
import com.grendelscan.categorizers.impl.ByRepeatableOutputContextCategorizer;
import com.grendelscan.categorizers.impl.ByResponseHeaderCategorizer;
import com.grendelscan.categorizers.impl.BySetCookieCategorizer;
import com.grendelscan.categorizers.impl.FinalAnalysisCategorizer;
import com.grendelscan.categorizers.impl.InitialAuthenticationCategorizer;

public class Categorizers
{
	final private ArrayList<Categorizer> allCategorizers;
	final private ArrayList<TransactionCategorizer> transactionCategorizers;

	final private AllTransactionsCategorizer allTransactionsCategorizerCategorizer;
	final private ByHtmlElementCategorizer byHtmlElementCategorizerCategorizer;
	final private ByHtmlFormCategorizer byHtmlFormCategorizer;
	final private ByHttpResponseCodeCategorizer byHttpResponseCodeCategorizerCategorizer;
	final private ByMimeTypeCategorizer byMimeTypeCategorizerCategorizer;
	final private ByResponseHeaderCategorizer byResponseHeaderCategorizerCategorizer;
	final private ByHostCategorizer byHostCategorizerCategorizer;
	final private ByHttpQueryCategorizer byHttpQueryCategorizer; 
	final private ByRequestDataLocationCategorizer byHttpQueryParameterCategorizer; 
	final private ByFileCategorizer byFileCategorizer;
	final private ByHttpMethodCategorizer byHttpMethodCategorizer;
	final private ByDirectoryCategorizer byDirectoryCategorizer;
	final private InitialAuthenticationCategorizer initialAuthenticationCategorizer;
	final private FinalAnalysisCategorizer finalAnalysisCategorizer;
	final private ByOutputContextCategorizer byOutputContextCategorizer;
	final private ByRepeatableOutputContextCategorizer byRepeatableOutputContextCategorizer;
	final private BySetCookieCategorizer bySetCookieCategorizer;
	final private ByBaseUriCategorizer byBaseUriCategorizer;
	final private ByQueryNamedDataCategorizer byQueryNamedDataCategorizer;
	
	public Categorizers()
	{
		// Change the starting element count in the HashSet
		// to match the number of categorizers being
		// initialized
		transactionCategorizers = new ArrayList<TransactionCategorizer>(8);
		allCategorizers = new ArrayList<Categorizer>(10);

		// Repeat for all categorizers used during the main
		// testing period. By host should always be first.

		byHostCategorizerCategorizer = new ByHostCategorizer();
		transactionCategorizers.add(byHostCategorizerCategorizer);
		allCategorizers.add(byHostCategorizerCategorizer);

		byFileCategorizer = new ByFileCategorizer();
		transactionCategorizers.add(byFileCategorizer);
		allCategorizers.add(byFileCategorizer);

		byDirectoryCategorizer = new ByDirectoryCategorizer();
		transactionCategorizers.add(byDirectoryCategorizer);
		allCategorizers.add(byDirectoryCategorizer);

		allTransactionsCategorizerCategorizer = new AllTransactionsCategorizer();
		transactionCategorizers.add(allTransactionsCategorizerCategorizer);
		allCategorizers.add(allTransactionsCategorizerCategorizer);

		byHtmlElementCategorizerCategorizer = new ByHtmlElementCategorizer();
		transactionCategorizers.add(byHtmlElementCategorizerCategorizer);
		allCategorizers.add(byHtmlElementCategorizerCategorizer);

		byHttpResponseCodeCategorizerCategorizer = new ByHttpResponseCodeCategorizer();
		transactionCategorizers.add(byHttpResponseCodeCategorizerCategorizer);
		allCategorizers.add(byHttpResponseCodeCategorizerCategorizer);

		byMimeTypeCategorizerCategorizer = new ByMimeTypeCategorizer();
		transactionCategorizers.add(byMimeTypeCategorizerCategorizer);
		allCategorizers.add(byMimeTypeCategorizerCategorizer);

		byResponseHeaderCategorizerCategorizer = new ByResponseHeaderCategorizer();
		transactionCategorizers.add(byResponseHeaderCategorizerCategorizer);
		allCategorizers.add(byResponseHeaderCategorizerCategorizer);

		byHttpQueryCategorizer = new ByHttpQueryCategorizer();
		transactionCategorizers.add(byHttpQueryCategorizer);
		allCategorizers.add(byHttpQueryCategorizer);

		byHttpQueryParameterCategorizer = new ByRequestDataLocationCategorizer();
		allCategorizers.add(byHttpQueryParameterCategorizer);
		transactionCategorizers.add(byHttpQueryParameterCategorizer);
		
		byOutputContextCategorizer = new ByOutputContextCategorizer();
		transactionCategorizers.add(byOutputContextCategorizer);
		allCategorizers.add(byOutputContextCategorizer);

		byHttpMethodCategorizer = new ByHttpMethodCategorizer();
		transactionCategorizers.add(byHttpMethodCategorizer);
		allCategorizers.add(byHttpMethodCategorizer);
		
		bySetCookieCategorizer = new BySetCookieCategorizer();
		transactionCategorizers.add(bySetCookieCategorizer);
		allCategorizers.add(bySetCookieCategorizer);
		
		byHtmlFormCategorizer = new ByHtmlFormCategorizer();
		transactionCategorizers.add(byHtmlFormCategorizer);
		allCategorizers.add(byHtmlFormCategorizer);
		
		byQueryNamedDataCategorizer = new ByQueryNamedDataCategorizer();
		transactionCategorizers.add(byQueryNamedDataCategorizer);
		allCategorizers.add(byQueryNamedDataCategorizer);
		
		
		// Technically, this is a transaction categorizer, but it isn't called for all transactions
		initialAuthenticationCategorizer = new InitialAuthenticationCategorizer();
		allCategorizers.add(initialAuthenticationCategorizer);
		
		
		// Non transaction categorizers go below here
//		byAuthenticationPackageCategorizerCategorizer = new ByAuthenticationPackageCategorizer();
//		allCategorizers.add(byAuthenticationPackageCategorizerCategorizer);
		
		finalAnalysisCategorizer = new FinalAnalysisCategorizer();
		allCategorizers.add(finalAnalysisCategorizer);

		byRepeatableOutputContextCategorizer = new ByRepeatableOutputContextCategorizer();
		allCategorizers.add(byRepeatableOutputContextCategorizer);
		
		byBaseUriCategorizer = new ByBaseUriCategorizer();
		allCategorizers.add(byBaseUriCategorizer);

	}

	public ByOutputContextCategorizer getByOutputContextCategorizer()
    {
    	return byOutputContextCategorizer;
    }

	public ArrayList<Categorizer> getAllCategorizers()
	{
		return allCategorizers;
	}

	public AllTransactionsCategorizer getAllTransactionsCategorizerCategorizer()
	{
		return allTransactionsCategorizerCategorizer;
	}


	public ByHtmlElementCategorizer getByHtmlElementCategorizerCategorizer()
	{
		return byHtmlElementCategorizerCategorizer;
	}

	public ByHttpResponseCodeCategorizer getByHttpResponseCodeCategorizerCategorizer()
	{
		return byHttpResponseCodeCategorizerCategorizer;
	}

	public ByMimeTypeCategorizer getByMimeTypeCategorizerCategorizer()
	{
		return byMimeTypeCategorizerCategorizer;
	}

	public ByResponseHeaderCategorizer getByResponseHeaderCategorizerCategorizer()
	{
		return byResponseHeaderCategorizerCategorizer;
	}

	public ByHostCategorizer getByHostCategorizerCategorizer()
	{
		return byHostCategorizerCategorizer;
	}

	public ArrayList<TransactionCategorizer> getTransactionCategorizers()
	{
		return transactionCategorizers;
	}


	public ByHttpQueryCategorizer getByHttpQueryCategorizer()
    {
    	return byHttpQueryCategorizer;
    }

	public ByFileCategorizer getByFileCategorizer()
    {
    	return byFileCategorizer;
    }

	public ByDirectoryCategorizer getByDirectoryCategorizer()
    {
    	return byDirectoryCategorizer;
    }

	public InitialAuthenticationCategorizer getInitialAuthenticationCategorizer()
    {
    	return initialAuthenticationCategorizer;
    }

	public FinalAnalysisCategorizer getFinalAnalysisCategorizer()
    {
    	return finalAnalysisCategorizer;
    }

	public ByRepeatableOutputContextCategorizer getByRepeatableOutputContextCategorizer()
    {
    	return byRepeatableOutputContextCategorizer;
    }

	public ByRequestDataLocationCategorizer getByHttpQueryParameterCategorizer()
    {
    	return byHttpQueryParameterCategorizer;
    }

	public ByHttpMethodCategorizer getByHttpMethodCategorizer()
    {
    	return byHttpMethodCategorizer;
    }

	public BySetCookieCategorizer getBySetCookieCategorizer()
    {
    	return bySetCookieCategorizer;
    }

	public final ByBaseUriCategorizer getByBaseUriCategorizer()
	{
		return byBaseUriCategorizer;
	}

	public final ByHtmlFormCategorizer getByHtmlFormCategorizer()
	{
		return byHtmlFormCategorizer;
	}

	public final ByQueryNamedDataCategorizer getByQueryDatumCategorizer()
	{
		return byQueryNamedDataCategorizer;
	}

}
