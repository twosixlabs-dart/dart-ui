import { envBool, envInt, envStr } from '../configUtils';

export default {
  enableLogging: envBool(process.env.DART_ENABLE_LOGGING, true),
  publicReportDuration: envInt(process.env.DART_PUBLIC_REPORT_DURATION, 3000),
  logMaxLength: envInt(process.env.DART_LOG_MAX_LENGTH, 10000),
  keycloakInit: {
    onLoad: 'check-sso',
    silentCheckSsoRedirectUri: `${window.location.origin}/silent-check-sso.html`,
    pkceMethod: 'S256',
  },
  keycloakParams: '/keycloak.json',
  disableAuth: envBool(process.env.DART_AUTH_BYPASS, false),
  basePath: envStr(process.env.DART_BASE_PATH, '/concepts/explorer'),
  common: {
    STORAGE_SUBSCRIPTION_THROTTLE_INTERVAL: 5000,
    DART_API_URL_BASE: `${process.env.PROCUREMENT_URL}/dart/api/v1`,
    USE_DART_AUTH: !(`${process.env.DART_AUTH_BYPASS}`.trim().toLowerCase() === 'true'),
  },
  dart: {
    COMPONENTS: [
      'forklift',
      // 'doc-status',
      'corpex',
    ],
  },
  forklift: {
    API_URL_BASE: `${process.env.PROCUREMENT_URL}/dart/api/v1/forklift`,
    SUBSCRIPTIONS: [
      // ['polledDocuments'],
    ],
  },
  corpex: {
    API_URL_BASE: `${process.env.SEARCH_URL}/dart/api/v1/corpex`,
    RAW_DOC_URL: process.env.RAW_DOC_URL,
    RAW_DOC_SOURCE: process.env.RAW_DOC_SOURCE || 'dart',
    DEFAULT_COMPONENTS: [
      'QUERY_STRING_SEARCH',
    ],
    SUPPORTED_COMPONENTS: [
      'QUERY_STRING_SEARCH',
      'TEXT_LENGTH_SEARCH',
      'DATE_SEARCH',
      'TERM_SEARCH',
      'ENTITY_SEARCH',
      'EVENT_SEARCH',
      'TOPIC_SEARCH',
      'FACTIVA_SEARCH',
      'BOOL_SEARCH',
    ],
    EXTRACTIONS: [
      'qntfy-ner',
      'qntfy-event',
    ],
    EXTRACTION_TYPES: {
      'qntfy-ner': [
        'EVENT',
        'FAC',
        'GPE',
        'LANGUAGE',
        'LAW',
        'LOC',
        'NORP',
        'ORG',
        'PERCENT',
        'PERSON',
      ],
      'qntfy-event': [
        'Agriculture',
        'Calculation',
        'Communication',
        'Conflict',
        'Expect',
        'Find',
        'FoodInsecurity',
        'HealthDisease',
        'Help',
        'Justice',
        'LifeEvent',
        'Manufacturing',
        'NegEmotions',
        'Observe',
        'Personnel',
        'PosEmotions',
        'RawMaterials',
        'Theft',
        'Trade',
        'Transport',
        'Transportation',
        'Violence',
        'Weather',
        'Conflict',
      ],
    },
    SUBSCRIPTIONS: [
      ['searchBuilder'],
    ],
  },
  docStatus: {
    SUBSCRIPTIONS: [
      // ['documents'],
    ],
  },
};