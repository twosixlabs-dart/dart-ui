import React, { Component } from 'react';

import { withStyles } from '@material-ui/core';
import Container from '@material-ui/core/Container';
import PropTypes from 'prop-types';

import CorpexRoot from './components/corpexRoot/components/CorpexRoot';
import { connect } from '../dart-ui/context/CustomConnect';
import { chooseTenant } from '../dart-ui/redux/actions/dart.actions';

const styles = () => ({
  container: {
    padding: 0,
    height: '100%',
  },
});

class CorpexUi extends Component {
  componentDidMount() {
    const { tenantId, tenants, dispatch } = this.props;

    if (tenantId === null) {
      if (tenants && tenants.length > 0) {
        dispatch(chooseTenant(tenants[0]));
      }
    }
  }

  componentDidUpdate(prevProps) {
    const prevTenants = prevProps.tenants;
    // eslint-disable-next-line react/destructuring-assignment
    const newTenants = this.props.tenants;
    const { dispatch, tenantId } = this.props;

    if (tenantId !== null
      && tenantId !== undefined
      && newTenants.length !== prevTenants.length
      && newTenants.length > 0) {
      dispatch(chooseTenant(newTenants[0]));
    }
  }

  render() {
    const {
      docView,
      documentId,
      classes,
    } = this.props;

    return (
      <Container maxWidth="xl" className={`corpex-ui ${classes.container}`}>
        <CorpexRoot docView={docView} documentId={documentId} />
      </Container>
    );
  }
}

CorpexUi.propTypes = {
  docView: PropTypes.bool.isRequired,
  documentId: PropTypes.string,
  classes: PropTypes.shape({
    container: PropTypes.string.isRequired,
  }).isRequired,
  tenantId: PropTypes.string,
  tenants: PropTypes.arrayOf(PropTypes.string).isRequired,
  dispatch: PropTypes.func.isRequired,
};
CorpexUi.defaultProps = {
  documentId: '',
  tenantId: null,
};

const mapStateToProps = (state, dartContext) => ({
  tenantId: state.dart.nav.tenantId,
  tenants: dartContext.tenants,
});

export default connect(mapStateToProps)(withStyles(styles)(CorpexUi));
