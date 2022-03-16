export const componentTypes = {
  NER_COMPONENT: 'NER_COMPONENT',
  EVENTS_COMPONENT: 'EVENTS_COMPONENT',
  TOPIC_COMPONENT: 'TOPIC_COMPONENT',
  FACTIVA_FACETS_COMPONENT: 'FACTIVA_FACETS_COMPONENT',
  DATE_TIME_COMPONENT: 'DATE_TIME_COMPONENT',
  TEXT_LENGTH_COMPONENT: 'TEXT_LENGTH_COMPONENT',
  KEYWORD_METADATA_COMPONENT: 'KEYWORD_METADATA_COMPONENT',
  SENTIMENT_SUBJECTIVITY_COMPONENT: 'SENTIMENT_SUBJECTIVITY_COMPONENT',
  SECTION_COMPONENT: 'SECTION_COMPONENT',
};

export const componentData = {
  [componentTypes.NER_COMPONENT]: {
    label: 'Named Entities',
  },
  [componentTypes.EVENTS_COMPONENT]: {
    label: 'Events',
  },
  [componentTypes.TOPIC_COMPONENT]: {
    label: 'Topics',
  },
  [componentTypes.SENTIMENT_STANCE_COMPONENT]: {
    label: 'Sentiment and Stance',
  },
  [componentTypes.FACTIVA_FACETS_COMPONENT]: {
    label: 'Factiva Facets',
  },
  [componentTypes.DATE_TIME_COMPONENT]: {
    label: 'Date/Time',
  },
  [componentTypes.TEXT_LENGTH_COMPONENT]: {
    label: 'Text Length',
  },
  [componentTypes.KEYWORD_METADATA_COMPONENT]: {
    label: 'Metadata Keywords',
  },
  [componentTypes.SECTION_COMPONENT]: {
    label: 'New Section',
  },
  [componentTypes.SENTIMENT_SUBJECTIVITY_COMPONENT]: {
    label: 'Sentiment/Subjectivity',
  },
};
