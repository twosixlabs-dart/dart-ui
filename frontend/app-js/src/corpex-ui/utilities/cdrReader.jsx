import React from 'react';
import { toPairs, fromPairs } from 'lodash';
import { Button } from '@material-ui/core';

import downloadCdr from '../components/documentView/components/cdrView/utilities/downloadCdr';

const cdrTest = (cdr) => cdr !== null && cdr !== undefined;

const extrMetaTest = (cdr) => cdrTest(cdr)
  && cdr.extracted_metadata !== null
  && cdr.extracted_metadata !== undefined;

const mfTest = (cdr, field) => extrMetaTest(cdr)
  && cdr.extracted_metadata[field] !== null
  && cdr.extracted_metadata[field] !== undefined;

const fTest = (cdr, field) => cdrTest(cdr)
  && cdr[field] !== null
  && cdr[field] !== undefined;

const f = (cdr, field) => (fTest(cdr, field) ? cdr[field] : '');

const mf = (cdr, field) => (mfTest(cdr, field) ? cdr.extracted_metadata[field] : '');

const formatDate = (dateText) => {
  if (dateText === '') return '';
  const dt = new Date(dateText);
  const dateTimeFormat = new Intl
    .DateTimeFormat('en', { year: 'numeric', month: 'long', day: 'numeric' });
  return `${dateTimeFormat.format(dt)}`;
};

const formatDateTime = (dateTimeText) => {
  if (dateTimeText === '') return '';
  const dt = new Date(dateTimeText);
  const dateTimeFormat = new Intl.DateTimeFormat(
    'en',
    {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric',
      timeZone: 'UTC',
      timeZoneName: 'short',
    },
  );

  return `${dateTimeFormat.format(dt)}`;
};

export const docId = (cdr) => f(cdr, 'document_id');
export const extractedText = (cdr) => f(cdr, 'extracted_text');
export const docIdLink = (xhrHandler, logger, cdr, tenantIdOrNull) => {
  const docIdIn = docId(cdr);
  return (
    <Button
      onClick={() => downloadCdr(xhrHandler, logger, docIdIn, tenantIdOrNull)}
    >
      {docIdIn}
    </Button>
  );
};

export const title = (cdr) => (mf(cdr, 'Title')).replace(/\n/g, ' ').trim();
export const shortTitle = (cdr) => {
  const fullTitle = title(cdr);
  return fullTitle.length < 100 ? fullTitle : `${fullTitle.slice(0, 100)} ...`;
};

export const description = (cdr) => mf(cdr, 'Description');
export const shortDesc = (cdr) => {
  const desc = description(cdr);
  return desc.length < 175 ? desc : `${desc.slice(0, 175)} ...`;
};

export const pubDate = (cdr) => formatDate(mf(cdr, 'CreationDate'));
export const capSrc = (cdr) => f(cdr, 'capture_source');
export const contType = (cdr) => f(cdr, 'content_type');
export const docType = (cdr) => mf(cdr, 'Type');
export const creator = (cdr) => mf(cdr, 'Creator');
export const producer = (cdr) => mf(cdr, 'Producer');
export const author = (cdr) => mf(cdr, 'Author');
export const genre = (cdr) => mf(cdr, 'StatedGenre');
export const subject = (cdr) => mf(cdr, 'Subject');
export const pages = (cdr) => mf(cdr, 'Pages');
export const classification = (cdr) => mf(cdr, 'Classification');
export const language = (cdr) => mf(cdr, 'OriginalLanguage');
export const publisher = (cdr) => mf(cdr, 'Publisher');
export const timestamp = (cdr) => formatDateTime(f(cdr, 'timestamp'));
export const team = (cdr) => f(cdr, 'team');
export const sourceUri = (cdr) => f(cdr, 'source_uri');
export const labels = (cdr) => {
  const labelsIn = f(cdr, 'labels');
  if (labelsIn === '') return '';
  return labelsIn.map((l) => l.trim()).join(', ');
};

export const wordCount = (result) => f(result, 'word_count');
export const facets = (result) => {
  const cdrFacets = f(result, 'facets');
  if (cdrFacets === '') return cdrFacets;
  const newFacets = {};
  Object.keys(cdrFacets).forEach((facetLabel) => {
    newFacets[facetLabel] = cdrFacets[facetLabel]
      .filter((facet) => facet.score === undefined
        || facet.score === null
        || facet.score >= 0.2)
      .sort((a, b) => {
        let res = -1;
        if (a.score !== null
          && a.score !== undefined
          && b.score !== null
          && b.score !== undefined) {
          res = a.score < b.score ? 1 : -1;
        } else if (a.value > b.value) res = 1;

        return res;
      });

    if (result.facets[facetLabel].length === 0) {
      Reflect.deleteProperty(result.facets, facetLabel);
    }

    newFacets[facetLabel] = newFacets[facetLabel]
      .map((facet) => {
        if (facet.score !== null
          && facet.score !== undefined) {
          return `${facet.value} (${facet.score.toFixed(2)})`;
        }
        return facet.value;
      }).join(', ');
  });

  return newFacets;
};

export const aggs = (result) => {
  const aggsIn = f(result, 'aggregations');
  if (aggsIn === '') return aggsIn;
  return fromPairs(toPairs(aggsIn)
    .map(([label, values]) => [label, values
      .map((agg) => `${agg.value} (${agg.count})`)
      .join(', ')]));
};
