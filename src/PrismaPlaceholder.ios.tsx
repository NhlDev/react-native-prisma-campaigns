import React from 'react';
import { requireNativeComponent } from 'react-native';
import { type ViewStyle } from 'react-native';

const NativeView = requireNativeComponent('PrismaCampaignsView');

export const PrismaPlaceholder = (props: PrismaProps) => {
    // Retorno con el banner envuelto en un modal, o sin modal
    return <NativeView {...props} />;
};

interface PrismaProps {
    placeholderName: string;
    style: ViewStyle;
}
