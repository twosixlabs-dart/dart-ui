import React, { Component } from 'react';
import { connect } from 'react-redux';
import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import Paper from '@material-ui/core/Paper';
import PropTypes from 'prop-types';

const styles = () => ({
  cdrWrapper: {
    maxWidth: '75ch',
    margin: 'auto',
  },
  paper: {
    height: '100%',
    overflowY: 'auto',
    padding: 20,
  },
});

const hashText = (text) => {
  const len = text.length;
  if (len < 10) return text;
  return text.substring(0, 3) + text[Math.floor(len / 2)] + text.substring(len - 4, len - 1);
};

class RawDocViewer extends Component {
  render() {
    const { cdr, classes } = this.props;

    const cdrViewer = cdr === null || cdr.extracted_text === undefined ? '' : (
      <Typography variant="body1">
        {document.cdr.extracted_text.split('\n').map((text) => (<p key={`cdr-text-${hashText(text)}`}>{text}</p>))}
      </Typography>
    );

    return (
      /* <CdrViewer/> */
      <Paper className={`raw-doc-viewer ${classes.paper}`}>
        <div className={classes.cdrWrapper}>
          <Typography variant="body1">
            {cdrViewer}
          </Typography>
        </div>
      </Paper>
    );
  }
}

RawDocViewer.propTypes = {
  cdr: PropTypes.shape({
    extracted_text: PropTypes.string,
  }).isRequired,
  classes: PropTypes.shape({
    paper: PropTypes.string.isRequired,
    cdrWrapper: PropTypes.string.isRequired,
  }).isRequired,
};

function mapStateToProps(state) {
  return {
    cdr: state.corpex.documentView.cdr,
  };
}

export default connect(mapStateToProps)(withStyles(styles)(RawDocViewer));
