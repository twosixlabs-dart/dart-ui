import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';

import corpusViewPropTypes from '../../../../corpusView.propTypes';
import { aggTypes } from '../../../../../searchBuilder/searchComponentData/enums';
import HistogramGraph from '../HistogramGraph';
import RangeInput from '../../../../../../../common/components/RangeInput';

const NUMBUCKETS = 150;

const formatNumber = (kind) => (num) => {
  if (kind === 'date') {
    return new Date(num).toDateString();
  }

  if (kind === 'date-time') {
    const date = new Date(num);
    return `${date.toDateString()} ${(date.getHours() + 1) % 13}:${date.getMinutes() < 10 ? `0${date.getMinutes()}` : date.getMinutes()} ${date.getHours() < 12 ? 'AM' : 'PM'}`;
  }

  return num;
};

const getBucketSize = (kind, currentBucketSize, data) => {
  if (data.length < 100 || data.length > 300) {
    const newSize = (data.length / 150) * currentBucketSize;

    if (kind === 'date' || kind === 'date-time') {
      if (newSize > 31536000000) return `${(newSize - (newSize % 31536000000)) / 31536000000}y`;
      if (newSize > 2592000000) return `${(newSize - (newSize % 2592000000)) / 2592000000}M`;
      if (newSize > 604800000) return `${(newSize - (newSize % 604800000)) / 604800000}w`;
      if (newSize > 86400000) return `${(newSize - (newSize % 86400000)) / 86400000}d`;
      if (newSize > 3600000) return `${(newSize - (newSize % 3600000)) / 3600000}h`;
      if (newSize > 60000) return `${(newSize - (newSize % 60000)) / 60000}m`;
      if (newSize > 1000) return `${(newSize - (newSize % 1000)) / 1000}s`;
      if (newSize > 20) return `${(newSize - (newSize % 20))}ms`;
      return '20ms';
    }

    if (kind !== 'date' && kind !== 'date-time') {
      if (kind === 'integer') return newSize === 0 ? 1 : Math.round(newSize);
      return (NUMBUCKETS / data.length) * currentBucketSize;
    }
  }

  return currentBucketSize;
};

export const aggId = 'number_histogram';

export const getAggQuery = (field, bucketSize, lo, hi) => ({
  agg_type: aggTypes.FIELD,
  queried_field: field,
  bucket_size: bucketSize,
  lo,
  hi,
});

const styles = () => ({
});

function NumberComponent({
  aggResults,
  outerWidth,
  currentData,
  updateData,
}) {
  const data = aggId in aggResults ? aggResults[aggId] : [];

  const {
    state: {
      loBound,
      hiBound,
      defaultBucketSize,
      bucketSize,
      min,
      max,
      field,
      initialized,
      changing,
      kind,
    },
  } = currentData;

  const getNumValue = (val) => {
    if (kind === 'date' || kind === 'date-time') {
      const date = new Date(val);
      return date.getTime();
    }

    return val;
  };

  const updateBoundsState = ([newLo, newHi]) => {
    updateData({
      state: {
        ...currentData.state,
        loBound: newLo,
        hiBound: newHi,
      },
    });
  };

  const updateBounds = ([newLo, newHi]) => {
    let newBucketSize = bucketSize;
    const bucketRate = data.length === 0
      ? 0.0000001 : (getNumValue(data[data.length - 1].hi) - getNumValue(data[0].lo));
    if ((newHi - newLo) * bucketRate > 8000) newBucketSize = defaultBucketSize;
    updateData({
      aggs: {
        [aggId]: getAggQuery(field, newBucketSize, newLo, newHi),
      },
      state: {
        ...currentData.state,
        bucketSize: newBucketSize,
        loBound: newLo,
        hiBound: newHi,
      },
    });
  };

  useEffect(() => {
    let currentBucketSizeAsNum = bucketSize;
    if (kind === 'date' || kind === 'date-time') {
      if (data.length > 0) {
        currentBucketSizeAsNum = Math.round(
          (getNumValue(data[0].hi) - getNumValue(data[0].lo)),
        );
      } else if (!Number.isNaN(Number(bucketSize))) {
        currentBucketSizeAsNum = bucketSize;
      } else {
        let unit;
        switch (bucketSize[bucketSize.length - 1]) {
          case 'ms':
            unit = 1;
            break;
          case 's':
            unit = 1000;
            break;
          case 'm':
            unit = 60000;
            break;
          case 'h':
            unit = 3600000;
            break;
          case 'd':
            unit = 86400000;
            break;
          case 'w':
            unit = 604800000;
            break;
          case 'M':
            unit = 2592000000;
            break;
          case 'y':
            unit = 31536000000;
            break;
          default:
            unit = 1;
            break;
        }
        currentBucketSizeAsNum = Number(bucketSize.substring(0, bucketSize.length - 1)) * unit;
      }
    }
    const newBucketSize = getBucketSize(kind, currentBucketSizeAsNum, data);
    if (!initialized && data.length > 0) {
      const newMin = getNumValue(data[0].lo);
      const newMax = getNumValue(data[data.length - 1].hi);
      updateData({
        aggs: {
          [aggId]: getAggQuery(field, newBucketSize, data[0].lo, data[data.length - 1].hi),
        },
        state: {
          ...currentData.state,
          bucketSize: newBucketSize,
          defaultBucketSize: bucketSize === newBucketSize ? newBucketSize : bucketSize,
          loBound: newMin,
          hiBound: newMax,
          min: newMin,
          max: newMax,
          initialized: bucketSize === newBucketSize,
          changing: !(bucketSize === newBucketSize),
        },
      });
    } else if ((changing || bucketSize !== newBucketSize)
      && min !== undefined && max !== undefined) {
      if (bucketSize !== newBucketSize) {
        updateData({
          aggs: {
            [aggId]: getAggQuery(field, newBucketSize, loBound, hiBound),
          },
          state: {
            ...currentData.state,
            bucketSize: newBucketSize,
            changing: !(bucketSize === newBucketSize),
          },
        });
      } else {
        updateData({
          state: {
            ...currentData.state,
            changing: !(bucketSize === newBucketSize),
          },
        });
      }
    }
  }, [data]);

  return (
    <div style={{ width: outerWidth }} className="corpus-overview-component">
      <HistogramGraph
        values={data}
        kind={kind}
      />
      <RangeInput
        width="100%"
        domain={[min, max]}
        disabled={!initialized}
        values={[loBound, hiBound]}
        onChange={updateBoundsState}
        onChangeCommitted={updateBounds}
        format={formatNumber(kind)}
      />
    </div>
  );
}

NumberComponent.propTypes = {
  aggs: PropTypes.shape({}).isRequired,
  aggResults: corpusViewPropTypes.aggResults.isRequired,
  fields: PropTypes.shape({}).isRequired,
  outerWidth: PropTypes.number.isRequired,
  currentData: corpusViewPropTypes.componentData.isRequired,
  updateData: PropTypes.func.isRequired,
};

const mapStateToProps = (state) => ({
  fields: state.corpex.corpexRoot.fields,
});

export default connect(mapStateToProps)(withStyles(styles)(NumberComponent));
