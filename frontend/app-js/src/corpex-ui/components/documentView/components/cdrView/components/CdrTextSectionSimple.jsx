import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import { makeSpan } from './CdrTextSection';

function CdrTextSectionSimple(props) {
  const {
    text,
  } = props;

  return (
    <div className="cdr-text-item-section-simple">
      {makeSpan(text)}
    </div>
  );
}

CdrTextSectionSimple.propTypes = {
  text: PropTypes.string.isRequired,
};

const mapStateToProps = (state) => ({
  tagMarkers: state.corpex.documentView.cdrView.extractions.tagMarkers,
});

export default connect(mapStateToProps)(CdrTextSectionSimple);
