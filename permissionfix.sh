#!/bin/sh

chmod +x gradlew

find build -type f -name "*.sh" -print0 | while IFS= read -r -d '' file; do
        echo "Setting executable permission for: $file"
        chmod +x "$file"
done

find iosApp -type f -name "*.sh" -print0 | while IFS= read -r -d '' file; do
        echo "Setting executable permission for: $file"
        chmod +x "$file"
done