import types from './corpexRoot.types';

const initState = {
  tags: {},
  facets: {},
  fields: {},
};

function corpexCorpexRootReducer(state = initState, action) {
  switch (action.type) {
    case types.COMPLETE_GET_FACET_IDS: {
      const newFacets = {};
      action.results.forEach((res) => {
        newFacets[res.facet_id] = res;
      });
      return { ...state, facets: newFacets };
    }

    case types.COMPLETE_GET_TAG_IDS: {
      const newTags = {};
      action.results.forEach((res) => {
        if (res.tag_id in newTags) {
          newTags[res.tag_id] = {
            ...newTags[res.tag_id],
            ...res,
          };
        } else {
          newTags[res.tag_id] = {
            ...res,
            tagTypes: {},
          };
        }
      });
      return { ...state, tags: newTags };
    }

    case types.COMPLETE_GET_TAG_TYPES: {
      const newTags = { ...state.tags };
      const tagTypes = {};
      action.results.forEach((tt) => {
        tagTypes[tt.tag_type] = tt;
      });
      if (action.tagId in newTags) {
        Object.values(newTags[action.tagId].tagTypes).forEach((tt) => {
          if (!(tt.tag_type in tagTypes)) tagTypes[tt.tag_type] = tt;
        });
        newTags[action.tagId] = { ...state.tags[action.tagId], tagTypes };
      } else newTags[action.tagId] = { tagTypes };
      return { ...state, tags: newTags };
    }

    case types.COMPLETE_GET_FIELD_IDS: {
      const newFields = { ...state.fields };
      action.results.forEach((res) => {
        newFields[res.field_id] = res;
      });
      return { ...state, fields: newFields };
    }

    default: {
      return state;
    }
  }
}

export default corpexCorpexRootReducer;
