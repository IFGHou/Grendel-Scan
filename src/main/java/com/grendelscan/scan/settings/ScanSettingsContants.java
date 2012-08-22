package com.grendelscan.scan.settings;

public interface ScanSettingsContants
{
	public static final String DEFAULT_SCAN_CONFIG_DIR = "scan-configs";
	
	
	public static final String AUTHENTICATION__CREDENTIALS = "authentication.credentials(";
	public static final String AUTHENTICATION__CREDENTIALS_PASSWORD = ").password";
	public static final String AUTHENTICATION__CREDENTIALS_USERNAME = ").username";
	public static final String AUTHENTICATION__METHOD = "authentication.form_based.method";
	public static final String AUTHENTICATION__PASSWORD_PARAMETER = "authentication.form_based.password_parameter_name";
	public static final String AUTHENTICATION__POST_QUERY = "authentication.form_based.post_query";
	public static final String AUTHENTICATION__URI = "authentication.form_based.uri";
	public static final String AUTHENTICATION__LOGOUT_URI = "authentication.form_based.logout_uri";
	public static final String AUTHENTICATION__USER_PARAMETER = "authentication.form_based.user_parameter_name";
	public static final String AUTHENTICATION__TYPE = "authentication.form_based.type";
	public static final String AUTHENTICATION__TYPE__FORM = "form";
	public static final String AUTHENTICATION__TYPE__HTTP = "http";
	public static final String AUTHENTICATION__USE_AUTHENTICATION = "authentication.use_authentication";
	public static final String AUTHENTICATION__AUTOMATIC_AUTHENTICATION = "authentication.automatic_authentication";

	public static final String HTTP_CLIENT__MAX_CONNECTIONS_PER_SERVER = "http_client.max_connections_per_server";
	public static final String HTTP_CLIENT__MAX_CONSECUTIVE_FAILED_REQUESTS = "http_client.max_consecutive_failed_requests";
	public static final String HTTP_CLIENT__MAX_FAILED_REQUESTS_PER_SERVER = "http_client.max_failed_requests_per_server";
	public static final String HTTP_CLIENT__MAX_FILE_SIZE = "http_client.max_file_size";
	public static final String HTTP_CLIENT__MAX_REDIRECTS = "http_client.defaults.max_redirects";
	public static final String HTTP_CLIENT__MAX_REQUESTS_PER_SECOND = "http_client.max_requests_per_second";
//	public static final String HTTP_CLIENT__MAX_REQUEST_COUNT = "http_client.max_request_count";
	public static final String HTTP_CLIENT__MAX_REQUEST_DEPTH = "http_client.max_request_depth";
	public static final String HTTP_CLIENT__MAX_REQUEST_RETRIES = "http_client.max_request_retries";
	public static final String HTTP_CLIENT__MAX_TOTAL_CONNECTIONS = "http_client.max_total_connections";
	public static final String HTTP_CLIENT__SOCKET_READ_TIMEOUT = "http_client.socket_read_timeout";
	public static final String HTTP_CLIENT__SOCKS_PROXY__USE_SOCKS_PROXY = "http_client.socks_proxy.use";
	public static final String HTTP_CLIENT__SOCKS_PROXY__SOCKS_PORT = "http_client.socks_proxy.port";
	public static final String HTTP_CLIENT__SOCKS_PROXY__SOCKS_HOST = "http_client.socks_proxy.host";
	public static final String HTTP_CLIENT__UPSTREAM_PROXY__ADDRESS = "http_client.upstream_proxy.address";
	public static final String HTTP_CLIENT__UPSTREAM_PROXY__PASSWORD = "http_client.upstream_proxy.password";
	public static final String HTTP_CLIENT__UPSTREAM_PROXY__PORT = "http_client.upstream_proxy.port";
	public static final String HTTP_CLIENT__UPSTREAM_PROXY__USERNAME = "http_client.upstream_proxy.username";
	public static final String HTTP_CLIENT__UPSTREAM_PROXY__USE_UPSTREAM_PROXY = "http_client.upstream_proxy.use_upstream_proxy";
	public static final String HTTP_CLIENT__USER_AGENT_STRING = "http_client.user_agent_string";

	public static final String PROXY_SETTINGS__ALLOW_ALL_PROXY_REQUESTS = "proxy_settings.allow_all_proxy_requests";
	public static final String PROXY_SETTINGS__INTERCEPT_REQUESTS = "proxy_settings.intercept_requests";
	public static final String PROXY_SETTINGS__INTERCEPT_RESPONSES = "proxy_settings.intercept_responses";
	public static final String PROXY_SETTINGS__MAX_PROXY_THREADS = "proxy_settings.max_proxy_threads";
	public static final String PROXY_SETTINGS__PROXY_BIND_ADDRESS = "proxy_settings.proxy_bind_address";
	public static final String PROXY_SETTINGS__PROXY_BIND_PORT = "proxy_settings.proxy_bind_port";
	public static final String PROXY_SETTINGS__PROXY_ENABLED = "proxy_settings.proxy_enabled";
	public static final String PROXY_SETTINGS__REVEAL_HIDDEN_FIELDS = "proxy_settings.reveal_hidden_fields";
	public static final String PROXY_SETTINGS__TEST_INTERCEPTED_REQUESTS = "proxy_settings.test_intercepted_requests";
	public static final String PROXY_SETTINGS__TEST_PROXY_REQUESTS = "proxy_settings.test_proxy_requests";

	public static final String SCAN_SETTINGS__BASE_URIS = "scan_settings.base_uris(";
	public static final String SCAN_SETTINGS__COMPAIRISONS__PARSE_HTML_DOM = "scan_settings.compairisons.parse_html_dom";
	public static final String SCAN_SETTINGS__QUEUES__MAX_CATEGORIZER_THREADS = "scan_settings.queues.max_categorizer_threads";
	public static final String SCAN_SETTINGS__QUEUES__MAX_REQUESTER_THREADS = "scan_settings.queues.max_requester_threads";
	public static final String SCAN_SETTINGS__QUEUES__MAX_TESTER_THREADS = "scan_settings.queues.max_tester_threads";
	public static final String SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__BASE = "scan_settings.response_code_overrides.manual_overrides(";
	public static final String SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__PATTERN = ").pattern";
	public static final String SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__STATUS_CODE = ").status_code";
	public static final String SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__TEST_ALL_DIRECTORIES = "scan_settings.response_code_overrides.test_all_directories";
	public static final String SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__THRESHOLD = "scan_settings.response_code_overrides.override_threshold";
	public static final String SCAN_SETTINGS__RESPONSE_CODE_OVERRIDES__USE_AUTOMATIC_OVERRIDES = "scan_settings.response_code_overrides.use_automatic_overrides";
	public static final String SCAN_SETTINGS__RESTRICTIONS__FORBIDDEN_PARAMETERS = "scan_settings.restrictions.forbidden_parameter_names(";
	public static final String SCAN_SETTINGS__RESTRICTIONS__IRRELEVANT_PARAMETERS = "scan_settings.restrictions.irrelevant_parameters(";
	public static final String SCAN_SETTINGS__RESTRICTIONS__URL_BLACKLIST = "scan_settings.restrictions.url_blacklist(";
	public static final String SCAN_SETTINGS__RESTRICTIONS__URL_WHITELIST = "scan_settings.restrictions.url_whitelist(";

	public static final String SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__BASE = "scan_settings.restful.patterns(";
	public static final String SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__NAME = ").name";
	public static final String SCAN_SETTINGS__RESTFUL_QUERY_PATTERNS__PATTERN = ").pattern";

	public static final String STORAGE_SETTINGS__TRANSACTION_DIRECTORY = "storage_settings.transaction_directory";
//	public static final String STORAGE_SETTINGS__OUTPUT_DIRECTORY = "storage_settings.output_directory";
	public static final String STORAGE_SETTINGS__DB_FILE = "storage_settings.database_file";
	
	public static final String REPORT_FORMAT__TEXT = "text";
	public static final String REPORT_FORMAT__HTML = "html";

	public static final String SESSION_TRACKING__KNOWN_SESSION_ID_PATTERNS = "session_tracking.known_session_id_patterns(";
//	public static final String SESSION_TRACKING__USE_LOGGED_OUT_DETECTION = "session_tracking.use_logged_out_detection";
	
	public static final String TEST_MODULES = "test_modules(";
	public static final String TEST_MODULES__MODULE_CLASS = ").module_class";
	public static final String TEST_MODULES__MODULE_NAME = ").module_name";
	public static final String TEST_MODULES__ENABLED = ").enabled";
	public static final String TEST_MODULES__OPTIONS = ").options(";
	public static final String TEST_MODULES__OPTION_NAME = ").option_name";
	public static final String TEST_MODULES__OPTION_VALUE = ").option_value";
	
	public static final String REVERSE_PROXIES = "reverse_proxies(";
	public static final String REVERSE_PROXIES__LOCAL_IP = ").local_ip";
	public static final String REVERSE_PROXIES__LOCAL_PORT = ").local_port";
	public static final String REVERSE_PROXIES__WEB_HOSTNAME = ").web_hostname";
	public static final String REVERSE_PROXIES__SSL = ").ssl";
	public static final String REVERSE_PROXIES__REMOTE_HOST = ").remote_host";
	public static final String REVERSE_PROXIES__REMOTE_PORT = ").remote_port";

}
