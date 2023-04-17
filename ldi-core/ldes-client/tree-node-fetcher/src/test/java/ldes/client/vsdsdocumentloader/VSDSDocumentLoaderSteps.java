package ldes.client.vsdsdocumentloader;

class VSDSDocumentLoaderSteps {

//	private static final Lang lang = Lang.JSONLD;
//
//	private VSDSDocumentLoader documentLoader;
//	private Context context;
//
//	@Given("I have a VSDSDocumentLoader")
//	public void iHaveAVsdsDocumentLoader() {
//		documentLoader = new VSDSDocumentLoader();
//	}
//
//	@When("I configure the Jena Context to use the custom DocumentLoader")
//	public void iConfigureTheJenaContextToUseTheCustomDocumentLoader() {
//		JsonLdOptions options = new JsonLdOptions();
//		options.setDocumentLoader(documentLoader);
//
//		context = new Context();
//		context.set(LangJSONLD10.JSONLD_OPTIONS, options);
//	}
//
//	@And("I modelize json ld content fetched from url {string}")
//	public void iModelizeJsonLdContentFetchedFromUrl(String url) {
//		RequestExecutor requestExecutor = new DefaultConfig().createRequestExecutor();
//		TreeNodeRequest treeNodeRequest = new TreeNodeRequest(url, lang, null);
//
//		Response response = requestExecutor.execute(treeNodeRequest.createRequest());
//
//		RDFParserBuilder.create().context(context).fromString(response.getBody().orElseThrow()).forceLang(Lang.JSONLD)
//				.toModel();
//	}
//
//	@Then("the configured DocumentLoader is used")
//	public void theConfiguredDocumentLoaderIsUsed() {
//		assertEquals(1, VSDSDocumentLoader.counter, "The VSDSDocumentLoader is used to load json context");
//	}
//
//	private class VSDSDocumentLoader extends DocumentLoader {
//
//		private static int counter = 0;
//
//		@Override
//		public RemoteDocument loadDocument(String url) throws JsonLdError {
//			counter++;
//
//			return super.loadDocument(url);
//		}
//	}
}
