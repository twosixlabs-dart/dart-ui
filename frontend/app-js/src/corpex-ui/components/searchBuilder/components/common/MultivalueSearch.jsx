import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { withStyles } from '@material-ui/core/styles';
import TextQuery from '../../../../../common/components/TextQuery';

const styles = (theme) => ({
  root: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(2),
  },
});

class MultivalueSearch extends Component {
  render() {
    const {
      onChange,
      query,
      classes,
    } = this.props;

    return (
      <div
        className={`search-values-query ${classes.root}`}
      >
        <TextQuery
          justifyContent="center"
          textValue={query}
          onType={onChange}
        />
      </div>
    );
  }
}

MultivalueSearch.propTypes = {
  query: PropTypes.string.isRequired,
  onChange: PropTypes.func.isRequired,
  classes: PropTypes.shape({
    root: PropTypes.string.isRequired,
  }).isRequired,
};

export default withStyles(styles)(MultivalueSearch);
