package com.example.emakumovil.communications;

import java.util.EventListener;

public interface ArrivedPackageListener extends EventListener {
    public void validPackage(ArrivedPackageEvent e);

}
