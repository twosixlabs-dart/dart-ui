import React, { Suspense } from 'react';
import PropTypes from 'prop-types';
import {
  JsDartContextProvider as JsDartContextProviderImport,
  ReduxProvider as ReduxProviderImport,
} from './dart-ui/context/contextProvider';
import FullSizeCentered from './common/components/layout/FullSizeCentered';

const CorpexUiImport = React.lazy(() => import('./corpex-ui/CorpexUi'));
const ForkliftUiImport = React.lazy(() => import('./forklift-ui/ForkliftUi'));

export const CorpexUi = (props) => (
  // eslint-disable-next-line react/destructuring-assignment
  <Suspense fallback={<FullSizeCentered>{props.loader}</FullSizeCentered>}>
    <CorpexUiImport {...props} />
  </Suspense>
);
CorpexUi.propTypes = {
  loader: PropTypes.node.isRequired,
};

export const ForkliftUi = (props) => (
  // eslint-disable-next-line react/destructuring-assignment
  <Suspense fallback={<FullSizeCentered>{props.loader}</FullSizeCentered>}>
    <ForkliftUiImport {...props} />
  </Suspense>
);
ForkliftUi.propTypes = {
  loader: PropTypes.node.isRequired,
};

export const ReduxProvider = ReduxProviderImport;
export const JsDartContextProvider = JsDartContextProviderImport;
