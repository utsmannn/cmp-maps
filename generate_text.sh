#!/bin/bash

# Nama file output
output_file="output.txt"

# Hapus file output kalo udah ada sebelumnya
if [ -f "$output_file" ]; then
    rm "$output_file"
fi

# Daftar ekstensi file yang dianggap sebagai source code
valid_extensions=("kt" "kts" "xml" "java" "py" "js" "html" "css" "cpp" "c" "h" "json" "yml" "yaml" "md")

# Fungsi buat ngecek apakah file readable text dan punya ekstensi yang valid
is_valid_source_file() {
    local file="$1"
    local ext="${file##*.}"

    # Cek apakah file bisa dibaca sebagai teks
    if file "$file" | grep -q "text"; then
        # Cek apakah file punya ekstensi yang valid
        for valid_ext in "${valid_extensions[@]}"; do
            if [[ "$ext" == "$valid_ext" ]]; then
                return 0
            fi
        done
    fi

    return 1
}

# Fungsi buat parse file
parse_file() {
    local file="$1"
    local relative_path="${file#$root_dir/}"

    echo "===" >> "$output_file"
    echo "Path: $relative_path" >> "$output_file"
    echo "===" >> "$output_file"
    cat "$file" >> "$output_file"
    echo "" >> "$output_file"
    echo "=== end $relative_path ===" >> "$output_file"
    echo "" >> "$output_file"
}

# Root folder
root_dir=$(pwd)

# Gunakan git ls-files untuk mendapatkan file yang di-track oleh Git (kecuali yang di-ignore)
git ls-files -z | while IFS= read -r -d '' file; do
    if [[ -f "$file" ]]; then
        if is_valid_source_file "$file"; then
           echo "Processing: $file"
            parse_file "$file"
        else
            echo "Skipping non-source or binary file: $file"
        fi
    fi
done

echo "Proses selesai! File $output_file udah digenerate."
