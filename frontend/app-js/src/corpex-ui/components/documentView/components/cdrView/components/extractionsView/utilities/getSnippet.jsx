import React from 'react';
import Typography from '@material-ui/core/Typography';

export default function getSnippet(text, offsets) {
  const SNIPPET_BUFFER = 20;
  const preferredStart = offsets[0] - SNIPPET_BUFFER;
  const preferredEnd = offsets[1] + SNIPPET_BUFFER + 1;

  const len = text.length;
  const start = preferredStart < 0 ? 0 : preferredStart;
  const ellipsesBefore = preferredStart < 0 ? '' : (<span>...</span>);
  const end = preferredEnd >= len ? len : preferredEnd;
  const ellipsesAfter = preferredEnd >= len ? '' : (<span>...</span>);

  const textBefore = text.substring(start, offsets[0]);
  const extraction = text.substring(offsets[0], offsets[1]);
  const textAfter = text.substring(offsets[1], end);

  return (
    <Typography variant="body1" component="p">
      {ellipsesBefore}
      <span>{textBefore}</span>
      <span style={{ fontWeight: 'bold' }}>{extraction}</span>
      <span>{textAfter}</span>
      {ellipsesAfter}
    </Typography>
  );
}
