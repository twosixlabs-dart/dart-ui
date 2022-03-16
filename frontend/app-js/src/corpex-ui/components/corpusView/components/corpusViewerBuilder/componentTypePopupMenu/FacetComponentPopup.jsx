import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';

import withStyles from '@material-ui/core/styles/withStyles';
import componentTypes from '../../../componentData/corpusViewComponentTypes';
import PopupMenuFrame from './commonComponents/PopupMenuFrame';
import { aggId, getAggQuery } from '../../corpusViewerComponent/dataComponents/facetValuesComponent/FacetValuesCorpusViewerComponent';
import SelectInput from './commonComponents/SelectInput';

const styles = () => ({
  root: {},
});

function FacetComponentPopup({
  sectionId,
  addComponent,
  closePopup,
  facets,
  facetIds,
  kind,
  labelLabel,
  valueLabel,
  classes,
}) {
  let type = componentTypes.FACET_COMPONENT;
  if (kind === 'with-score') type = componentTypes.FACET_CONFIDENCE_AVG_COMPONENT;

  const [componentData, setComponentData] = React.useState({
    type,
    label: `Facet Values: ${facets[facetIds[0]].label}`,
    layout: {
      x: 0,
      y: 0,
      w: 4,
      h: 6,
      isResizable: false,
    },
    aggs: {
      [aggId]: getAggQuery(
        kind === 'with-score'
          ? 'score-avg' : 'no-score',
        facetIds[0],
        0.5,
        1.0,
      ),
    },
    state: {
      facetId: facetIds[0],
      labelLabel,
      valueLabel,
      scoreLo: 0.5,
      scoreHi: 1.0,
      kind: kind === 'with-score' ? 'score-avg' : 'no-score',
    },
  });

  const {
    state: {
      facetId,
      scoreLo,
      scoreHi,
    },
  } = componentData;

  const displayType = componentData.state.kind;

  const supportedFacets = Object.values(facets).filter((f) => facetIds.includes(f.facet_id))
    .map((v) => [v.facet_id, v.label]);

  const facetChangeHandler = (newFacetId, newScoreLo, newScoreHi) => {
    setComponentData({
      ...componentData,
      label: `Facet Values: ${facets[newFacetId].label}`,
      aggs: {
        [aggId]: getAggQuery(displayType, newFacetId, newScoreLo, newScoreHi),
      },
      state: {
        ...componentData.state,
        facetId: newFacetId,
        scoreLo: newScoreLo,
        scoreHi: newScoreHi,
      },
    });
  };

  const displayTypeHandler = (newDisplayType) => {
    setComponentData({
      ...componentData,
      type: newDisplayType === 'score-filter' ? componentTypes.FACET_CONFIDENCE_FILTER_COMPONENT : componentTypes.FACET_CONFIDENCE_AVG_COMPONENT,
      aggs: {
        [aggId]: getAggQuery(newDisplayType, facetId, scoreLo, scoreHi),
      },
      state: {
        ...componentData.state,
        kind: newDisplayType,
      },
    });
  };

  return (
    <PopupMenuFrame
      data={componentData}
      closePopup={closePopup}
      sectionId={sectionId}
      addComponent={addComponent}
    >
      <div className={classes.root}>
        {kind !== 'with-score' ? '' : (
          <SelectInput
            label="Display:"
            supportedFields={[['score-avg', 'Average Score'], ['score-filter', 'Number of Documents']]}
            onChange={displayTypeHandler}
            value={displayType}
          />
        )}
        {supportedFacets.length <= 1 ? '' : (
          <SelectInput
            label="Field"
            supportedFields={supportedFacets}
            onChange={facetChangeHandler}
            value={facetId}
          />
        )}
      </div>
    </PopupMenuFrame>
  );
}

FacetComponentPopup.propTypes = {
  sectionId: PropTypes.oneOf([undefined, PropTypes.string]).isRequired,
  labelLabel: PropTypes.string.isRequired,
  valueLabel: PropTypes.oneOf([PropTypes.string, undefined]).isRequired,
  addComponent: PropTypes.func.isRequired,
  closePopup: PropTypes.func.isRequired,
  facets: PropTypes.objectOf(PropTypes.shape({
    label: PropTypes.string,
  })).isRequired,
  facetIds: PropTypes.arrayOf(PropTypes.string).isRequired,
  kind: PropTypes.oneOf(['no-score', 'with-score']).isRequired,
  classes: PropTypes.arrayOf(PropTypes.string).isRequired,
};

const mapStateToProps = (state) => ({
  facets: state.corpex.corpexRoot.facets,
});

export default connect(mapStateToProps)(withStyles(styles)(FacetComponentPopup));
