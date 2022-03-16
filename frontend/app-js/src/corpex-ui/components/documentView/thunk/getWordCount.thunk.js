// import xhrHandler from '../../../../common/redux/thunk/xhrHandler';
import { API_URL_BASE } from '../../../config/constants';
import { boolTypes, queryTypes } from '../../searchBuilder/searchComponentData/enums';
import { completeWordCountQuery } from '../documentView.actions';

function getWordCount(xhrHandler, docId) {
  return (dispatch, getState) => {
    const tenantIdorNull = getState().dart.nav.tenantId;
    const url = `${API_URL_BASE}/search`;

    const body = JSON.stringify({
      queries: [
        {
          query_type: queryTypes.TERM,
          bool_type: boolTypes.MUST,
          queried_field: 'cdr.document_id',
          term_values: [docId],
        },
      ],
      page: 0,
      page_size: 1,
      fields: ['word_count'],
      tenant_id: tenantIdorNull,
    });
    const method = 'POST';

    const startAction = () => ({ type: null });
    const completeAction = (res) => completeWordCountQuery(res);
    const errorAction = () => ({ type: null });

    // eslint-disable-next-line max-len
    xhrHandler(method, url, body, startAction, completeAction, errorAction, dispatch, getState());
  };
}

export default getWordCount;
