import React, { useEffect, useState } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import { Grid, Typography } from '@material-ui/core';
import Button from '@material-ui/core/Button';
import ExpandMoreIcon from '@material-ui/icons/ExpandMore';
import ExpandLessIcon from '@material-ui/icons/ExpandLess';
import usePrevious from '../../../../../../common/utilities/usePrevious';

const styles = () => ({
  outline: {
    borderBottom: '1px dotted darkgray',
    zIndex: 2,
  },
  histogramBar: {
    position: 'absolute',
    backgroundColor: 'lightgray',
    left: 0,
    top: 0,
    height: '100%',
    zIndex: 1,
  },
  dataLine: {
    whiteSpace: 'nowrap',
    overflowX: 'hidden',
    paddingLeft: 5,
    paddingRight: 5,
  },
  pageUp: {
    width: '100%',
    height: 18,
    marginBottom: 5,
  },
  pageDown: {
    width: '100%',
    height: 18,
    marginTop: 5,
  },
  parent: {
    position: 'relative',
  },
});

const HistogramTable = (props) => {
  const {
    classes,
    values,
    labelLabel,
    valueLabel,
    getLabel,
    getValue,
    displayValue,
    sort,
    take,
    nopage,
    count,
  } = props;

  const [dataOffset, setDataOffset] = useState(0);

  const prevValues = usePrevious(values);
  useEffect(() => {
    if (prevValues !== undefined) {
      if (prevValues.length !== values.length) {
        setDataOffset(0);
      } else if (prevValues.length > 0
        && (prevValues[0] !== values[0]
          || prevValues[values.length - 1] !== values[values.length - 1])) {
        setDataOffset(0);
      }
    }
  });

  const maxValue = Math.max(...values.map((valObj) => getValue(valObj)));
  const proportionMax = count === undefined || count < maxValue ? maxValue : count;
  let sortedValues;
  let multiplier = 1;
  switch (sort) {
    case 'none':
      sortedValues = values;
      break;
    case 'asc':
      multiplier = -1;
    // eslint-disable-next-line no-fallthrough
    default:
      sortedValues = values.sort((o1, o2) => multiplier * (o2.num_docs - o1.num_docs));
  }

  const finalValues = sortedValues.slice(dataOffset, dataOffset + take);
  const page = (offset) => () => {
    const newOffset = dataOffset + offset;
    if (newOffset !== dataOffset) {
      if (newOffset < 0) {
        setDataOffset(0);
      } else if (newOffset + take >= sortedValues.length) {
        if (dataOffset !== sortedValues.length - take) {
          setDataOffset(sortedValues.length - take);
        }
      } else {
        setDataOffset(newOffset);
      }
    }
  };

  return (
    <div className="histogram-table">
      <Grid container direction="column" spacing={0}>
        <Grid item xs={12}>
          <Grid container direction="row">
            <Grid item xs={8}>
              {labelLabel}
            </Grid>
            <Grid item xs={4}>
              {valueLabel}
            </Grid>
          </Grid>
        </Grid>
        {nopage || sortedValues.length < take ? '' : (
          <Grid item xs={12}>
            <Button
              onClick={page(0 - take)}
              disabled={dataOffset === 0}
              classes={{ root: classes.pageUp }}
            >
              <ExpandLessIcon />
            </Button>
          </Grid>
        )}
        {finalValues.map((valObj) => (
          <Grid item xs={12} key={`${valObj.value}-${valObj.num_docs}`}>
            <Grid container direction="row" className={classes.parent}>
              <div className={classes.histogramBar} style={{ width: `${(100 * getValue(valObj)) / proportionMax}%` }} />
              <Grid item xs={8} className={classes.outline}>
                <Grid container justifyContent="flex-start">
                  <div className={classes.dataLine}>
                    <Typography>{getLabel(valObj)}</Typography>
                  </div>
                </Grid>
              </Grid>
              <Grid item xs={4} className={classes.outline}>
                <Grid container justifyContent="flex-end">
                  <div className={classes.dataLine}>
                    <Typography>{displayValue(getValue(valObj))}</Typography>
                  </div>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        ))}
        {nopage || sortedValues.length < take ? '' : (
          <Grid item xs={12}>
            <Button
              onClick={page(take)}
              disabled={dataOffset >= sortedValues.length - take}
              classes={{ root: classes.pageDown }}
            >
              <ExpandMoreIcon />
            </Button>
          </Grid>
        )}
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
  sort: PropTypes.oneOf(['desc', 'asc', 'none']),
  take: PropTypes.number,
  nopage: PropTypes.bool,
  labelLabel: PropTypes.string,
  valueLabel: PropTypes.string,
  getLabel: PropTypes.func,
  getValue: PropTypes.func,
  displayValue: PropTypes.func,
  count: PropTypes.oneOf([PropTypes.number, undefined]).isRequired,
};

HistogramTable.defaultProps = {
  sort: 'desc',
  take: 20,
  nopage: false,
  labelLabel: '',
  valueLabel: '',
  getLabel: (valObj) => valObj.value,
  getValue: (valObj) => valObj.num_docs,
  displayValue: (val) => val,
};

export default withStyles(styles)(HistogramTable);
