import Typography from '@material-ui/core/Typography';
import React from 'react';

const formatEventDescription = (str) => {
  if (typeof str !== 'string') return str;

  const firstSplit = str.split('[[');

  if (firstSplit.length !== 2) return (<Typography variant="body1">{str}</Typography>);

  const secondSplit = firstSplit[1].split(']]');

  if (secondSplit.length !== 2) return (<Typography variant="body1">{str}</Typography>);

  return (
    <Typography variant="body1">
      {firstSplit[0]}
      <b>{secondSplit[0]}</b>
      {secondSplit[1]}
    </Typography>
  );
};

export default formatEventDescription;
