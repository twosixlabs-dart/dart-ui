import React from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import { Grid, Tooltip, Typography } from '@material-ui/core';

const styles = () => ({
  histogramBar: {
    backgroundColor: 'darkgray',
  },
  histogramBucket: {
    cursor: 'pointer',
    '&:hover': {
      backgroundColor: 'lightblue',
      outline: '1px solid blue',
    },
    '&:hover .histo-bar': {
      backgroundColor: 'darkblue',
    },
  },
  label: {
    overflowX: 'visible',
  },
  parent: {
    position: 'relative',
  },
});

const genLabel = (value, kind) => {
  let label;

  switch (kind) {
    case 'number': {
      label = value.toString();
      break;
    }

    case 'date': {
      const date = new Date(value);
      const dateStr = date.toDateString();
      label = dateStr.substr(dateStr.indexOf(' ') + 1);
      break;
    }

    case 'date-time': {
      const date = new Date(value);
      const dateStr = date.toLocaleDateString('en-US');
      const timeStr = date.toLocaleTimeString('en-Us');
      label = `${dateStr} ${timeStr}`;
      break;
    }

    default: {
      label = value.toString();
    }
  }

  return label;
};

function getWidth(bucketWidth, barsPerLabel, barIndex) {
  const width = bucketWidth * barsPerLabel;
  const totalWidth = bucketWidth * barIndex + width;
  const adjustWidth = totalWidth > 100 ? totalWidth - 100 : 0;
  return width - adjustWidth;
}

const HistogramTable = (props) => {
  const {
    classes,
    numLabels,
    values,
    barHeight,
    kind,
  } = props;

  const maxValue = Math.max(...values.map((valObj) => valObj.num_docs));
  const sortedValues = values.sort((o1, o2) => o1.lo - o2.lo);
  const numBars = sortedValues.length;
  const bucketWidth = 100 / sortedValues.length;
  const barsPerLabel = Math.floor(numBars / numLabels);

  const bars = (
    <Grid container dir="row" justifyContent="space-evenly" alignItems="flex-end">
      {sortedValues.map((valObj) => (
        <Tooltip
          placement="top"
          title={`${genLabel(valObj.lo, kind)} - ${genLabel(valObj.hi, kind)}: ${valObj.num_docs} documents`}
        >
          <Grid
            item
            style={{ width: `${bucketWidth}%`, height: barHeight }}
            classes={{ root: classes.histogramBucket }}
            key={`bar-${valObj.num_docs}-${valObj.lo}`}
          >
            <div
              style={{ width: '100%', height: barHeight * (1 - (valObj.num_docs / maxValue)) }}
            />
            <div
              style={{ width: '100%', height: barHeight * (valObj.num_docs / maxValue) }}
              className={`${classes.histogramBar} histo-bar`}
            />
          </Grid>
        </Tooltip>
      ))}
    </Grid>
  );

  const labels = (
    <Grid container dir="row" justifyContent="space-evenly" alignItems="flex-start">
      {sortedValues.map((valObj, i) => {
        let labelElem;
        if (i % barsPerLabel === 0) {
          labelElem = (
            <Grid
              item
              classes={{ root: classes.label }}
              style={{ width: `${getWidth(bucketWidth, barsPerLabel, i)}%` }}
              key={`label-${valObj.num_docs}-${valObj.lo}`}
            >
              |
              <Typography>
                {genLabel(valObj.lo, kind)}
              </Typography>
            </Grid>
          );
        } else labelElem = '';
        return labelElem;
      })}
    </Grid>
  );

  return (
    <div className="histogram-table">
      <Grid container direction="column">
        <Grid item xs={12}>{bars}</Grid>
        <Grid item xs={12}>{labels}</Grid>
      </Grid>
    </div>
  );
};

HistogramTable.propTypes = {
  values: PropTypes.arrayOf(PropTypes.shape({
    values: PropTypes.string,
    num_docs: PropTypes.number,
  })).isRequired,
  classes: PropTypes.objectOf(PropTypes.string).isRequired,
  numLabels: PropTypes.number,
  barHeight: PropTypes.number,
  kind: PropTypes.oneOf(['date', 'date-time', 'integer', 'float']),
};

HistogramTable.defaultProps = {
  numLabels: 10,
  barHeight: 50,
  kind: 'integer',
};

export default withStyles(styles)(HistogramTable);
