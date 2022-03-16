import {
  completeGetFacetIds,
  completeGetFieldIds,
  completeGetTagIds,
  completeGetTagTypes,
} from '../corpexRoot.actions';
// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';

function getApiData(xhrHandler) {
  return (dispatch, getState) => {
    const fieldIdUrl = `${API_URL_BASE}/fields`;
    const facetIdUrl = `${API_URL_BASE}/annotations/facets`;
    const tagIdUrl = `${API_URL_BASE}/annotations/tags`;
    const tagTypesUrl = (tagId) => `${API_URL_BASE}/annotations/tags/${tagId}/types`;
    const method = 'GET';
    const body = null;

    const startAction = () => ({ type: null });
    const errorAction = () => ({ type: null });
    const completeActionFields = (results) => completeGetFieldIds(results);
    const completeActionFacets = (results) => completeGetFacetIds(results);
    const completeActionTagTypes = (tagId) => (results) => completeGetTagTypes(tagId, results);
    const completeActionTags = (results) => {
      results.forEach((res) => {
        // eslint-disable-next-line max-len
        xhrHandler(method, tagTypesUrl(res.tag_id), body, startAction, completeActionTagTypes(res.tag_id), errorAction, dispatch, getState());
      });
      return completeGetTagIds(results);
    };

    // eslint-disable-next-line max-len
    xhrHandler(method, fieldIdUrl, body, startAction, completeActionFields, errorAction, dispatch, getState());
    // eslint-disable-next-line max-len
    xhrHandler(method, facetIdUrl, body, startAction, completeActionFacets, errorAction, dispatch, getState());
    // eslint-disable-next-line max-len
    xhrHandler(method, tagIdUrl, body, startAction, completeActionTags, errorAction, dispatch, getState());
  };
}

export default getApiData;
