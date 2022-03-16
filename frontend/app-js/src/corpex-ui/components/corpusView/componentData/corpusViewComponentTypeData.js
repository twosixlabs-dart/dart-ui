import corpusViewComponentTypes from './corpusViewComponentTypes';

export default {
  [corpusViewComponentTypes.SECTION_COMPONENT]: {
    label: 'Section',
  },
  [corpusViewComponentTypes.TAG_COMPONENT]: {
    label: 'Tags',
  },
  [corpusViewComponentTypes.FACET_COMPONENT]: {
    label: 'Facet',
  },
  [corpusViewComponentTypes.FACET_CONFIDENCE_FILTER_COMPONENT]: {
    label: 'Facet with Score Filter',
  },
  [corpusViewComponentTypes.FACET_CONFIDENCE_AVG_COMPONENT]: {
    label: 'Facet with Average Score',
  },
  [corpusViewComponentTypes.METADATA_KEYWORD_COMPONENT]: {
    label: 'Keyword',
  },
  [corpusViewComponentTypes.METADATA_NUMBER_COMPONENT]: {
    label: 'Number',
  },
  [corpusViewComponentTypes.METADATA_DATE_COMPONENT]: {
    label: '',
  },
};
