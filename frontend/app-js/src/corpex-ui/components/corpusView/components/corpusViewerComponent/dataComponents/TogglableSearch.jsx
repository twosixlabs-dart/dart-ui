import React, { useState } from 'react';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import SearchIcon from '@material-ui/icons/Search';
import Button from '@material-ui/core/Button';
import { Grid, Typography } from '@material-ui/core';

import OutsideClickHandler from 'react-outside-click-handler/esm/OutsideClickHandler';

import TextQuery from '../../../../../../common/components/TextQuery';

const styles = () => ({
  searchButton: {
    width: '100%',
    height: 25,
    marginTop: 2,
    marginBottom: 2,
  },
});

const TogglableSearch = (props) => {
  const {
    value,
    onChange,
    classes,
  } = props;

  const [expanded, setExpanded] = useState(false);

  const clickHandler = () => {
    if (!expanded) setExpanded(true);
  };

  const clickOffHandler = () => {
    if (value === '') setExpanded(false);
  };

  const expandedField = (
    <OutsideClickHandler
      onOutsideClick={clickOffHandler}
    >
      <Grid container direction="row" alignItems="center" justifyContent="center">
        <Grid item>
          <SearchIcon />
        </Grid>
        <Grid item xs={10}>
          <TextQuery
            textValue={value}
            onType={onChange}
          />
        </Grid>
      </Grid>
    </OutsideClickHandler>
  );

  const unexpandedField = (
    <Grid container alignItems="center">
      <Grid item xs={12}>
        <Button
          onClick={clickHandler}
          classes={{ root: classes.searchButton }}
        >
          <Grid container direction="row" alignItems="center" justifyContent="center" spacing={1}>
            <Grid item>
              <SearchIcon />
            </Grid>
            <Grid item>
              <Typography>
                Filter Values
              </Typography>
            </Grid>
          </Grid>
        </Button>
      </Grid>
    </Grid>
  );

  return expanded ? expandedField : unexpandedField;
};

TogglableSearch.propTypes = {
  value: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    searchButton: PropTypes.string,
  }).isRequired,
};

export default withStyles(styles)(TogglableSearch);
