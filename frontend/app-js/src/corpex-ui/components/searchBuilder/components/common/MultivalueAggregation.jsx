import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import Button from '@material-ui/core/Button';
import Grid from '@material-ui/core/Grid';
import Pagination from '@material-ui/lab/Pagination';

const styles = () => ({
  root: {},
});

class MultivalueAggregation extends Component {
  render() {
    const {
      onChange,
      page,
      pageSize,
      onPageChange,
      values,
    } = this.props;

    const numPages = Math.ceil(values.length / pageSize);
    const pageStart = page * pageSize;
    const pageOffset = pageStart + pageSize;
    const pageEnd = pageOffset > values.length ? values.length : pageOffset;

    return (
      <Grid container spacing={2}>
        {numPages < 2 ? '' : (
          <Grid item xs={12}>
            <Grid container justifyContent="center">
              <Pagination
                count={numPages}
                page={page + 1}
                onChange={onPageChange}
                className="agg-search-values-selector-pagination"
              />
            </Grid>
          </Grid>
        )}
        <Grid container spacing={1}>
          {values.slice(pageStart, pageEnd).map((valueObj) => (
            <Grid
              item
              key={`agg-value-${valueObj.value}`}
              xs="auto"
              sm="auto"
              md="auto"
              lg="auto"
            >
              <div
                id={`agg-value-${valueObj.value ? valueObj.value.replace(/\s/g, '-') : 'no-value'}`}
                className="agg-search-values-selector-agg-value"
              >
                <Button
                  size="small"
                  color={valueObj.selected ? 'primary' : 'default'}
                  variant={valueObj.selected ? 'contained' : 'outlined'}
                  onClick={() => {
                    const newValues = values.map((oldValueObj) => {
                      if (oldValueObj.value === valueObj.value) {
                        return { ...oldValueObj, selected: !valueObj.selected };
                      }
                      return oldValueObj;
                    });

                    onChange(newValues);
                  }}
                >
                  {`${valueObj.value} (${valueObj.count})`}
                </Button>
              </div>
            </Grid>
          ))}
        </Grid>
      </Grid>
    );
  }
}

MultivalueAggregation.propTypes = {
  values: PropTypes.arrayOf(PropTypes.shape({
    value: PropTypes.string.isRequired,
    count: PropTypes.number,
    selected: PropTypes.bool.isRequired,
  })).isRequired,
  onChange: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
  page: PropTypes.number.isRequired,
  pageSize: PropTypes.number.isRequired,
  onPageChange: PropTypes.func.isRequired,
};

export default withStyles(styles)(MultivalueAggregation);
