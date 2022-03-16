export const getTagIdFromLabel = (tagLabel, tags) => {
  let tagId = '';
  Object.values(tags).forEach((t) => {
    if (t.cdr_label === tagLabel) tagId = t.tag_id;
  });
  return tagId;
};

// eslint-disable-next-line no-unused-vars
export const getFacetIdFromLabel = (facetLabel, facets) => '';
