import React, { Component } from 'react';
// import { Redirect, withRouter } from 'react-router-dom';
import withStyles from '@material-ui/core/styles/withStyles';
import Grid from '@material-ui/core/Grid';
import ArrowBackIos from '@material-ui/icons/ArrowBackIos';
import Button from '@material-ui/core/Button';
import Tabs from '@material-ui/core/Tabs';
import Tab from '@material-ui/core/Tab';
import Paper from '@material-ui/core/Paper';
import Typography from '@material-ui/core/Typography';
import PropTypes from 'prop-types';
import StickyHeader from '../../../../common/components/layout/StickyHeader';
import DocumentPanel from './DocumentPanel';
import TwoPanel from '../../../../common/components/layout/TwoPanel';
import { docViewClearState, setDocumentView } from '../documentView.actions';
import MetadataPanel from './MetadataPanel';
import Header from '../../../../common/components/Header';
import getCdr from '../thunk/getCdr.thunk';
import getDocViewCdrAggregations from '../thunk/getDocViewCdrAggregations.thunk';
import getDocViewCdrFacets from '../thunk/getDocViewCdrFacets.thunk';
import getWordCount from '../thunk/getWordCount.thunk';
import { connect } from '../../../../dart-ui/context/CustomConnect';
import { setCdrTextArray } from './cdrView/cdrView.actions';
import AbsoluteCentered from '../../../../common/components/layout/AbsoluteCentered';

const styles = () => ({
  tabMenu: {
    position: 'relative',
    flexGrow: 1,
  },
});

function a11yProps(index) {
  return {
    id: `scrollable-auto-tab-${index}`,
    'aria-controls': `scrollable-auto-tabpanel-${index}`,
  };
}

class DocumentViewer extends Component {
  constructor(props) {
    super(props);

    this.fetchAll = this.fetchAll.bind(this);
  }

  componentDidMount() {
    this.fetchAll();
  }

  shouldComponentUpdate(newProps) {
    const newId = newProps.documentId;
    // eslint-disable-next-line react/destructuring-assignment
    const oldId = this.props.documentId;

    if (newId !== oldId) {
      this.fetchAll();
    }

    return true;
  }

  componentWillUnmount() {
    const { dispatch } = this.props;

    dispatch(docViewClearState());
  }

  fetchAll() {
    const { documentId, dispatch, xhrHandler } = this.props;
    dispatch(getCdr(xhrHandler, documentId));
    dispatch(getDocViewCdrAggregations(xhrHandler, documentId));
    dispatch(getDocViewCdrFacets(xhrHandler, documentId));
    dispatch(getWordCount(xhrHandler, documentId));
    dispatch(setCdrTextArray([]));
  }

  render() {
    const {
      back,
      documentId,
      view,
      dispatch,
      cdr,
      error,
      loader,
      classes,
    } = this.props;

    if (cdr === null) {
      if (error === '404') {
        return (
          <Grid container direction="column" justifyContent="center" alignItems="center">
            <Grid item>
              <Typography variant="h5">
                <span>Document </span>
                <span>
                  {documentId}
                </span>
                <span> does not exist</span>
              </Typography>
            </Grid>
          </Grid>
        );
      }

      return (
        <AbsoluteCentered>
          {loader}
        </AbsoluteCentered>
      );
    }

    const returnHandler = (e) => {
      e.nativeEvent.stopImmediatePropagation();
      e.stopPropagation();
      dispatch(docViewClearState());
      back();
    };

    const tabHandler = (e, v) => {
      if (v !== view) {
        dispatch(setDocumentView(v));
      }
    };

    const metadataPanelHeader = (
      <Header>
        <Button color="primary" onClick={returnHandler}>
          <ArrowBackIos color="primary" />
          <Typography variant="subtitle1" color="primary">
            <b>RETURN</b>
          </Typography>
        </Button>
      </Header>
    );

    const documentPanelHeader = (
      <Paper square className={classes.tabMenu}>
        <Tabs
          value={view}
          onChange={tabHandler}
          indicatorColor="primary"
          textColor="primary"
          variant="standard"
          scrollButtons="auto"
          aria-label="Tag Type"
          centered
        >
          {/* eslint-disable-next-line react/jsx-props-no-spreading */}
          <Tab label="Extracted Text" value="cdr" {...a11yProps(0)} />
          {/* eslint-disable-next-line react/jsx-props-no-spreading */}
          <Tab label="Raw Document" value="raw" {...a11yProps(1)} />
        </Tabs>
      </Paper>
    );

    const leftPanel = (
      <StickyHeader fixedBody header={metadataPanelHeader}>
        <MetadataPanel docId={documentId} />
      </StickyHeader>
    );

    const rightPanel = (
      <StickyHeader fixedBody header={documentPanelHeader}>
        <DocumentPanel docId={documentId} />
      </StickyHeader>
    );

    return (
      <TwoPanel
        squeezeLeft
        independentScroll
        left={leftPanel}
        right={rightPanel}
        className="document-viewer"
      />
    );
  }
}

DocumentViewer.propTypes = {
  loader: PropTypes.element.isRequired,
  back: PropTypes.func.isRequired,
  documentId: PropTypes.string.isRequired,
  dispatch: PropTypes.func.isRequired,
  view: PropTypes.string.isRequired,
  // textScroll: PropTypes.number.isRequired,
  cdr: PropTypes.shape({
    document_id: PropTypes.string,
  }),
  error: PropTypes.string,
  classes: PropTypes.shape({
    tabMenu: PropTypes.string.isRequired,
  }).isRequired,
  xhrHandler: PropTypes.func.isRequired,
};

DocumentViewer.defaultProps = {
  cdr: null,
  error: null,
};

function mapStateToProps(state, dartContext) {
  return {
    loader: dartContext.loader,
    back: dartContext.router.back,
    view: state.corpex.documentView.root.view,
    cdr: state.corpex.documentView.root.cdr,
    error: state.corpex.documentView.error,
    xhrHandler: dartContext.xhrHandler,
  };
}

// export default withRouter(connect(mapStateToProps)(withStyles(styles)(DocumentViewer)));
export default connect(mapStateToProps)(withStyles(styles)(DocumentViewer));
