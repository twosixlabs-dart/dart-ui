import {
  boolTypes, componentTypes, facetIds, tagIds,
} from '../components/searchBuilder/searchComponentData/enums';

const bootStrapComponent = (typeOfComponent) => {
  switch (typeOfComponent) {
    case componentTypes.QUERY_STRING_SEARCH: {
      return {
        title: 'Text Search',
        boolType: boolTypes.MUST,
        componentState: {
          queriedFields: [0],
          availableFields: [
            { field: 'cdr.extracted_text', label: 'Full Text' },
            { field: 'cdr.extracted_metadata.Description', label: 'Description' },
            { field: 'cdr.extracted_metadata.Title', label: 'Title' },
            { field: 'cdr.extracted_metadata.Author', label: 'Author' },
          ],
          query: '',
        },
      };
    }

    case componentTypes.TERM_SEARCH: {
      return {
        title: 'Metadata Keyword Search',
        boolType: boolTypes.FILTER,
        componentState: {
          queriedField: null,
          availableFields: [
            { field: 'cdr.content_type', label: 'Content Type' },
            { field: 'cdr.team', label: 'Team' },
            { field: 'cdr.capture_source', label: 'Capture Source' },
            { field: 'cdr.labels', label: 'Labels' },
            { field: 'cdr.extracted_metadata.StatedGenre', label: 'User-Defined Genre' },
            { field: 'cdr.extracted_metadata.PredictedGenre', label: 'Predicted Genre' },
            { field: 'cdr.extracted_metadata.Relevance', label: 'Relevance' },
            { field: 'cdr.extracted_metadata.Type', label: 'Document Type' },
            { field: 'cdr.extracted_metadata.OriginalLanguage', label: 'Original Language' },
            { field: 'cdr.extracted_metadata.Classification', label: 'Classification' },
            { field: 'cdr.extracted_metadata.Publisher', label: 'Publisher' },
            { field: 'cdr.extracted_metadata.Producer', label: 'Producer' },
            { field: 'cdr.extracted_metadata.Subject', label: 'Subject' },
            { field: 'cdr.extracted_metadata.Creator', label: 'Creator' },
          ],
          query: '',
          termValues: [],
          selectedTermValues: [],
          termBoolType: boolTypes.SHOULD,
          page: 0,
          numPages: 0,
        },
      };
    }

    case componentTypes.DATE_SEARCH: {
      return {
        title: 'Date Search',
        boolType: boolTypes.FILTER,
        componentState: {
          queriedFields: [0],
          availableFields: [
            { field: 'cdr.extracted_metadata.CreationDate', label: 'Publication' },
            { field: 'cdr.extracted_metadata.ModDate', label: 'Modification' },
            { field: 'cdr.timestamp', label: 'Timestamp' },
          ],
          lowerBound: null,
          upperBound: null,
        },
      };
    }

    case componentTypes.TEXT_LENGTH_SEARCH: {
      return {
        title: 'Text Length Filter',
        boolType: boolTypes.MUST,
        componentState: {
          queriedFields: [0],
          availableFields: [
            { field: 'word_count', label: 'Word Count', values: [{ lo: 0, hi: 500000 }] },
            { field: 'cdr.extracted_metadata.Pages', label: 'Page Count' },
          ],
          intLo: null,
          intHi: null,
          limitLo: 0,
          limitHi: 10,
        },
      };
    }

    case componentTypes.ENTITY_SEARCH: {
      return {
        title: 'Named Entities',
        boolType: boolTypes.MUST,
        componentState: {
          tagId: tagIds.ENTITY,
          tagTypes: [],
          selectedTagType: null,
          query: '',
          tagBoolType: boolTypes.MUST,
          page: 0,
          numPages: 0,
          tagValues: [],
          selectedTagValues: [],
        },
      };
    }

    case componentTypes.EVENT_SEARCH: {
      return {
        title: 'Events',
        boolType: boolTypes.MUST,
        componentState: {
          tagId: tagIds.EVENT,
          tagTypes: [],
          selectedTagType: null,
          query: '',
          tagBoolType: boolTypes.MUST,
          page: 0,
          numPages: 0,
          tagValues: [],
          selectedTagValues: [],
        },
      };
    }

    case componentTypes.TOPIC_SEARCH: {
      return {
        title: 'Topics',
        boolType: boolTypes.SHOULD,
        componentState: {
          facetIds: [],
          selectedFacetId: facetIds.QNTFY_TOPIC,
          query: '',
          scoreRange: [0.0, 1.0],
          facetBoolType: boolTypes.MUST,
          page: 0,
          numPages: 0,
          facetValues: [],
          selectedFacetValues: [],
        },
      };
    }

    case componentTypes.FACTIVA_SEARCH: {
      return {
        title: 'Factiva Facets',
        boolType: boolTypes.SHOULD,
        componentState: {
          facetIds: [facetIds.FACTIVA_SUBJECT, facetIds.FACTIVA_REGION, facetIds.FACTIVA_INDUSTRY],
          selectedFacetId: null,
          hasScore: null,
          query: '',
          scoreRange: [0, 1],
          facetBoolType: boolTypes.SHOULD,
          page: 0,
          numPages: 0,
          facetValues: [],
          selectedFacetValues: [],
        },
      };
    }

    case componentTypes.BOOL_SEARCH: {
      return {
        title: 'Boolean Sub-Search',
        boolType: boolTypes.MUST,
        componentState: {
          componentMap: [],
        },
      };
    }

    default: {
      return {};
    }
  }
};

export default bootStrapComponent;
