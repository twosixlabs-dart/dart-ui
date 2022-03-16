import { RAW_DOC_SOURCE, RAW_DOC_URL } from '../../../../../config/constants';

export default function getUrl(docId) {
  if (RAW_DOC_SOURCE === 'static') return RAW_DOC_URL;
  if (RAW_DOC_SOURCE === 'dart') return `${RAW_DOC_URL}/${docId}`;
  if (RAW_DOC_SOURCE === 'cauoogle') return `${RAW_DOC_URL}/${docId}/raw`;
  return null;
}
