package com.github.zlamb1.assignment1.view;

import com.github.zlamb1.assignment1.AddressInfo;
import com.github.zlamb1.view.IView;

import java.util.Collection;

public interface IAddressView extends IView {
    void displayAddressInfo(Collection<AddressInfo> addressInfoList);
}
