export const componentTypes = {
  QUERY_STRING_SEARCH: 'QUERY_STRING_SEARCH',
  TERM_SEARCH: 'TERM_SEARCH',
  DATE_SEARCH: 'DATE_SEARCH',
  TEXT_LENGTH_SEARCH: 'TEXT_LENGTH_SEARCH',
  ENTITY_SEARCH: 'ENTITY_SEARCH',
  EVENT_SEARCH: 'EVENT_SEARCH',
  TOPIC_SEARCH: 'TOPIC_SEARCH',
  FACTIVA_SEARCH: 'FACTIVA_SEARCH',
  BOOL_SEARCH: 'BOOL_SEARCH',
};

export const boolTypes = {
  MUST: 'MUST',
  MUST_NOT: 'MUST_NOT',
  SHOULD: 'SHOULD',
  FILTER: 'FILTER',
};

export const queryTypes = {
  TEXT: 'TEXT',
  TERM: 'TERM',
  INTEGER: 'INTEGER',
  CDR_DATE: 'CDR_DATE',
  TAG_DATE: 'TAG_DATE',
  FACET: 'FACET',
  TAG: 'TAG',
  BOOL: 'BOOL',
};

export const aggTypes = {
  FIELD: 'FIELD',
  TAG_TYPES: 'TAG_TYPES',
  TAG_VALUES: 'TAG_VALUES',
  FACET: 'FACET',
  FACET_CONFIDENCE: 'FACET_CONFIDENCE',
};

export const entityTypes = {
  PERSON: 'PERSON',
  NORP: 'NORP',
  FAC: 'FAC',
  ORG: 'ORG',
  GPE: 'GPE',
  LOC: ' LOC',
  PRODUCT: 'PRODUCT',
  EVENT: 'EVENT',
  WORK_OF_ART: 'WORK_OF_ART',
  LAW: 'LAW',
  LANGUAGE: 'LANGUAGE',
  DATE: 'DATE',
  TIME: 'TIME',
  PERCENT: 'PERCENT',
  MONEY: 'MONEY',
  QUANTITY: 'QUANTITY',
  ORDINAL: 'ORDINAL',
  CARDINAL: 'CARDINAL',
};

export const eventTypes = {
  B_ACTION: 'B_ACTION',
};

export const allTagTypes = { ...eventTypes, ...entityTypes };

export const tagIds = {
  ENTITY: 'qntfy-ner',
  EVENT: 'qntfy-event',
};

export const facetIds = {
  QNTFY_TOPIC: 'qntfy-topic',
  FACTIVA_REGION: 'factiva-region',
  FACTIVA_SUBJECT: 'factiva-subject',
  FACTIVA_INDUSTRY: 'factiva-industry',
};
