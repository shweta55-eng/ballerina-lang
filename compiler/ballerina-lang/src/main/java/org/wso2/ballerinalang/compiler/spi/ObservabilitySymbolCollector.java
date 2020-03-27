package org.wso2.ballerinalang.compiler.spi;

import org.wso2.ballerinalang.compiler.tree.BLangPackage;
import org.wso2.ballerinalang.compiler.util.CompilerContext;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Implementation of this interface analyze the AST of the Ballerina program
 * and generate symbol data to be used for observability purposes.
 */
public interface ObservabilitySymbolCollector {
    void init(CompilerContext context);

    void process(BLangPackage pkgNode);

    void writeCollectedSymbols(BLangPackage module, Path destination) throws IOException;
}
